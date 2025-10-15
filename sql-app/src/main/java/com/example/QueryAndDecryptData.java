//Copyright 2024 Google LLC
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//https://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.

package com.example;

import com.google.crypto.tink.Aead;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

public class QueryAndDecryptData {

    public static void main(String[] args) throws GeneralSecurityException, SQLException, IOException {
        // Saving credentials in environment variables is convenient, but not secure - consider a more
        // secure solution such as Cloud Secret Manager to help keep secrets safe.
        Config myconfig = new Config();

        // Initialize database connection pool and create table if it does not exist
        // See CloudSqlConnectionPool.java for setup details
        DataSource pool =
                CloudSqlConnectionPool.createConnectionPool(myconfig);

        // Initialize envelope AEAD
        // See CloudKmsEnvelopeAead.java for setup details
        Aead envAead = CloudKmsEnvelopeAead.get(myconfig.getKmsURI());

        queryAndDecryptData(pool, envAead, myconfig.getDbTableName());
    }

    public static void queryAndDecryptData(DataSource pool, Aead envAead, String tableName)
            throws GeneralSecurityException, SQLException {

        try (Connection conn = pool.getConnection()) {
            String stmt =
                    String.format(
                            "SELECT purchase_uuid, customer_id, full_name, email_address FROM %s LIMIT 5",
                            tableName);
            try (PreparedStatement salesStmt = conn.prepareStatement(stmt); ) {
                ResultSet salesResults = salesStmt.executeQuery();

                while (salesResults.next()) {
                    String uuidAAD = salesResults.getString(1);

                    // Use the envelope AEAD primitive to decrypt the email, using the team name as
                    // associated data. This binds the encryption of the email to the team name, preventing
                    // associating an encrypted email in one row with a team name in another row.
                    String customerId = new String(envAead.decrypt(salesResults.getBytes(2), uuidAAD.getBytes()));
                    String fullName = new String(envAead.decrypt(salesResults.getBytes(3), uuidAAD.getBytes()));
                    String emailAddress = new String(envAead.decrypt(salesResults.getBytes(4), uuidAAD.getBytes()));


                    System.out.println(String.format("%s\t%s\t%s\t%s", uuidAAD, customerId, fullName,emailAddress));
                }
            }
        }
    }
}