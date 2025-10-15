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

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTable {
    public static void main(String[] args) throws GeneralSecurityException, SQLException, IOException {
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
            System.err.println("Exception loading SQL create table file from Resources");
            System.err.println(ex.getMessage());
            return;
        }
        // Establish a connection
        Connection connection = pool.getConnection();

        // Create a Statement
        Statement statement = connection.createStatement();
        statement.executeUpdate(sqlStmt.toString());
        System.out.println("Table 'sales' created successfully");

        statement.close();
        connection.close();

    }
}
