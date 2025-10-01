package com.example;

import java.io.InputStream;
import java.util.Properties;

public class Config {
//    private   String dbUser = "postgres";
//    private   String dbPass = "Pa55w0rd019283";
//    private   String dbName = "acme";
//    private   String tableName = "sales";
//    private   String instanceConnectionName = "blog-465608:us-central1:blog";
//    private   String dbEndpoint = "34.134.69.20";
//    private   String dbPort = "5432";
//    private String dbSocketFactory = "com.google.cloud.sql.postgres.SocketFactory";
//
//    private String kmsURI = "gcp-kms://projects/blog-465608/locations/global/keyRings/blog/cryptoKeys/data-encrypt-decrypt";
//
//    private String tableDataFile = "/home/stamperj/gcp/blog/datasets/blog_mock_dataset_50cust_1000purch.csv";

    private Properties properties;

    public Config() {
        properties = new Properties();
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            properties.load(input);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getCreateTableFile() { return properties.getProperty("database.createtablefile"); }

    public String getDbUrl() { return properties.getProperty("database.url"); }

    public String getDbUser() {
        return properties.getProperty("database.username");
    }

    public String getDbPass() {
        return properties.getProperty("database.password");
    }

    public String getDbName() {
        return properties.getProperty("database.name");
    }

    public String getDbTableName() {
        return properties.getProperty("database.tablename");
    }

    public String getDbInstanceConnectionName() {
        return properties.getProperty("database.instanceconnectionname");
    }

    public String getDbEndpoint() {
        return properties.getProperty("database.endpoint");
    }

    public String getDbSocketFactory() {
        return properties.getProperty("database.socketfactory");
    }

    public String getKmsURI() {
        return properties.getProperty("kms.keyuri");
    }

    public String getTableDataFile() {
        return properties.getProperty("database.datafile");
    }

    public static void main(String[] args) {
        Config config = new Config();
        System.out.println(config.getDbPass());
        System.out.println(config.getKmsURI());
        System.out.println(config.getDbUrl());
    }
}
