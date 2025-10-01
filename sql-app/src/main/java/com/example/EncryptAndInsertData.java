package com.example;


import com.google.crypto.tink.Aead;

import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.sql.*;
import java.util.List;

public class EncryptAndInsertData {

    public static void main(String[] args) throws GeneralSecurityException, SQLException {
        // Saving credentials in environment variables is convenient, but not secure - consider a more
        // secure solution such as Cloud Secret Manager to help keep secrets safe.
        Config myconfig = new Config();
//        String dbUser = "postgres"; // e.g. "root", "postgres"
//        String dbPass = "Pa55w0rd019283"; // e.g. "mysupersecretpassword"
//        String dbName = "postgres"; // e.g. "votes_db"
//        String instanceConnectionName =
//                System.getenv("INSTANCE_CONNECTION_NAME"); // e.g. "project-name:region:instance-name"
//        String kmsUri = "gcp-kms://projects/blog-465608/locations/global/keyRings/blog/cryptoKeys/data-encrypt-decrypt"; // e.g. "gcp-kms://projects/...path/to/key
//        // Tink uses the "gcp-kms://" prefix for paths to keys stored in Google Cloud KMS. For more
//        // info on creating a KMS key and getting its path, see
//        // https://cloud.google.com/kms/docs/quickstart

//        String team = myconfig.getRowFieldTeam();
        String tableName = myconfig.getDbTableName();
//        String email = myconfig.getRowFieldEmail();

        // Initialize database connection pool and create table if it does not exist
        // See CloudSqlConnectionPool.java for setup details
        DataSource pool = CloudSqlConnectionPool.createConnectionPool(myconfig);
        //CloudSqlConnectionPool.createTable(pool, tableName);

        // Initialize envelope AEAD
        // See CloudKmsEnvelopeAead.java for setup details
        Aead envAead = CloudKmsEnvelopeAead.get(myconfig.getKmsURI());

        encryptAndInsertData(pool, envAead, tableName, myconfig);
    }

    public static void encryptAndInsertData(DataSource pool, Aead envAead, String tableName, com.example.Config config) throws GeneralSecurityException, SQLException {
        String filePath = config.getTableDataFile(); // Replace with your file path
        List<String> linesList = null;

        try {
            // Read all lines from the file into a List<String>
            linesList = Files.readAllLines(Paths.get(filePath));

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
