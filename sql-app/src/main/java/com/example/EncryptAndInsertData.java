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

import javax.sql.DataSource;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.sql.*;
import java.util.List;

public class EncryptAndInsertData {

    public static void main(String[] args) throws GeneralSecurityException, SQLException, IOException {
        // Saving credentials in environment variables is convenient, but not secure - consider a more
        // secure solution such as Cloud Secret Manager to help keep secrets safe.
        Config myconfig = new Config();
        String tableName = myconfig.getDbTableName();

        // Initialize database connection pool and create table if it does not exist
        // See CloudSqlConnectionPool.java for setup details
        DataSource pool = CloudSqlConnectionPool.createConnectionPool(myconfig);

        // Initialize envelope AEAD
        // See CloudKmsEnvelopeAead.java for setup details
        Aead envAead = CloudKmsEnvelopeAead.get(myconfig.getKmsURI());

        encryptAndInsertData(pool, envAead, tableName, myconfig);
    }

    private static File getResourceAsFile(String resourceName) throws IOException {
        InputStream inputStream = Config.class.getClassLoader().getResourceAsStream(resourceName);

        if (inputStream == null) {
            throw new IllegalArgumentException("Resource not found: " + resourceName);
        }

        // 1. Create a temporary file
        Path tempDir = Files.createTempDirectory("resource-temp");
        File tempFile = tempDir.resolve(resourceName).toFile();

        // 2. Copy the contents from the InputStream to the temporary file
        Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // Ensure the temporary directory and file are marked for deletion on exit
        tempFile.deleteOnExit();
        tempDir.toFile().deleteOnExit();

        return tempFile;
    }

    public static void encryptAndInsertData(DataSource pool, Aead envAead, String tableName, com.example.Config config) throws GeneralSecurityException, SQLException, IOException {
        File dataFile = getResourceAsFile(config.getTableDataFile());

        List<String> linesList = null;

        try {
            // Read all lines from the file into a List<String>
            linesList = Files.readAllLines(Paths.get(dataFile.getAbsolutePath()));

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        // Convert the List<String> to a String array
        List<String> subList = linesList.subList(1,linesList.size());
        String[] linesArray = subList.toArray(new String[0]);
        for (String line : linesArray) {
            System.out.println(line);
        }

        try (Connection conn = pool.getConnection()) {
            // Print the lines to verify
            for (String line : linesArray) {
                System.out.println(line);
                String[] data = line.split(",");
                String stmt = String.format("INSERT INTO %s (purchase_uuid,customer_id,full_name,email_address,purchase_date,product_id,product_category,quantity,price_per_unit,total_price) VALUES (?, ?, ?, ?, TO_DATE(?,'YYYY-MM-DD'), ?, ?, ?, ?, ?);", tableName);
                try (PreparedStatement salesStmt = conn.prepareStatement(stmt); ) {
                    salesStmt.setString(1, data[0]);
                    //encrypt customer_id with uuid as aead
                    byte[] encryptedCustomerId = envAead.encrypt(data[1].getBytes(),data[0].getBytes());
                    salesStmt.setBytes(2,encryptedCustomerId);

                    //encrypt full_name with uuid as aead
                    byte[] encryptedFullName = envAead.encrypt(data[2].getBytes(),data[0].getBytes());
                    salesStmt.setBytes(3,encryptedFullName);

                    //encrypt email_address with uuid as aead
                    byte[] encryptedEmailAddress = envAead.encrypt(data[3].getBytes(),data[0].getBytes());
                    salesStmt.setBytes(4,encryptedEmailAddress);

                    salesStmt.setString(5, data[4]);

                    salesStmt.setString(6,data[5]);
                    salesStmt.setString(7,data[6]);
                    salesStmt.setInt(8,Integer.parseInt(data[7]));
                    salesStmt.setBigDecimal(9,new BigDecimal(data[8]));
                    salesStmt.setBigDecimal(10,new BigDecimal(data[9]));


                    // Finally, execute the statement. If it fails, an error will be thrown.
                    salesStmt.execute();
                    System.out.println(String.format("Successfully inserted row into table %s", tableName));
                }
            }
        }
    }
}
