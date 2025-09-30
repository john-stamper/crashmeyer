package com.example;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;

public class CreateTable {
    public static void main(String[] args) throws GeneralSecurityException {
        Config myconfig = new Config();
        String tableName = myconfig.getDbTableName();
        DataSource pool = CloudSqlConnectionPool.createConnectionPool(myconfig);
        StringBuilder sqlStmt = new StringBuilder();

        try (InputStream input = Config.class.getClassLoader().getResourceAsStream(myconfig.getCreateTableFile())) {
            if (input == null) {
                System.out.println("Sorry, unable to find '"+myconfig.getCreateTableFile()+"'");
                return;
            }
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sqlStmt.append(line).append("\n");
                }
                System.out.println(sqlStmt);
            }
            catch (IOException ioe) {
                System.err.println("Error reading sql file: "+ioe.getMessage());
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
