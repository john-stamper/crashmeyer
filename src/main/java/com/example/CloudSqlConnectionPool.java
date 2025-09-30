package com.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;

public class CloudSqlConnectionPool {

    public static DataSource createConnectionPool(com.example.Config myconfig) throws GeneralSecurityException {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to load PostgreSQL JDBC Driver.", e);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format(myconfig.getDbUrl(),myconfig.getDbName(),myconfig.getDbInstanceConnectionName(),myconfig.getDbSocketFactory()));
        config.setUsername(myconfig.getDbUser()); // e.g. "root", "postgres"
        config.setPassword(myconfig.getDbPass()); // e.g. "my-password"
        DataSource pool = new HikariDataSource(config);
        return pool;
    }

//    public static void createTable(DataSource pool, String tableName) throws SQLException {
//        // Safely attempt to create the table schema.
//        try (Connection conn = pool.getConnection()) {
//            String stmt = String.format("CREATE TABLE IF NOT EXISTS %s ( "
//                    + "vote_id SERIAL NOT NULL, time_cast timestamp NOT NULL, team CHAR(6) NOT NULL,"
//                    + "voter_email BYTEA, PRIMARY KEY (vote_id) );", tableName);
//            try (PreparedStatement createTableStatement = conn.prepareStatement(stmt);) {
//                createTableStatement.execute();
//            }
//        }
//    }
    public static void main(String[] args) throws Exception{
        Config myconfig = new Config();
        DataSource dataSource = createConnectionPool(myconfig);
    }
}