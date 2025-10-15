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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.sql.DataSource;

public class CloudSqlConnectionPool {

    public static DataSource createConnectionPool(com.example.Config myconfig) throws GeneralSecurityException, IOException {

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
        try {
            config.setPassword(myconfig.getDbPass()); // e.g. "my-password"
        } catch (IOException e) {
            throw new RuntimeException("Failed to load DB password from Secrets Mgr",e);
        }
        DataSource pool = new HikariDataSource(config);
        return pool;
    }

    public static void main(String[] args) throws Exception{
        Config myconfig = new Config();
        DataSource dataSource = createConnectionPool(myconfig);
    }
}