package com.example;


import com.google.crypto.tink.Aead;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.sql.DataSource;

public class QueryAndDecryptData {

    public static void main(String[] args) throws GeneralSecurityException, SQLException {
        // Saving credentials in environment variables is convenient, but not secure - consider a more
        // secure solution such as Cloud Secret Manager to help keep secrets safe.
        Config myconfig = new Config();
//        String dbUser = System.getenv("DB_USER"); // e.g. "root", "postgres"
//        String dbPass = System.getenv("DB_PASS"); // e.g. "mysupersecretpassword"
//        String dbName = System.getenv("DB_NAME"); // e.g. "votes_db"
//        String instanceConnectionName =
//                System.getenv("INSTANCE_CONNECTION_NAME"); // e.g. "project-name:region:instance-name"
//        String kmsUri = System.getenv("CLOUD_KMS_URI"); // e.g. "gcp-kms://projects/...path/to/key
//        // Tink uses the "gcp-kms://" prefix for paths to keys stored in Google Cloud KMS. For more
//        // info on creating a KMS key and getting its path, see
//        // https://cloud.google.com/kms/docs/quickstart
//
//        String tableName = "votes123";

        // Initialize database connection pool and create table if it does not exist
        // See CloudSqlConnectionPool.java for setup details
        DataSource pool =
                CloudSqlConnectionPool.createConnectionPool(myconfig);
        //CloudSqlConnectionPool.createTable(pool, myconfig.getTableName());

        // Initialize envelope AEAD
        // See CloudKmsEnvelopeAead.java for setup details
        Aead envAead = CloudKmsEnvelopeAead.get(myconfig.getKmsURI());

        // Insert row into table to test
        // See EncryptAndInsert.java for setup details
//        EncryptAndInsertData.encryptAndInsertData(
//                pool, envAead, myconfig.getTableName(), myconfig);

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