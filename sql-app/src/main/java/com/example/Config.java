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

import java.io.InputStream;
import java.util.Properties;

public class Config {
    private final Properties properties;

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

    public String getDbPass() { return properties.getProperty("database.password")};

    public String getDbName() {
        return properties.getProperty("database.name");
    }

    public String getDbTableName() {
        return properties.getProperty("database.tablename");
    }

    public String getDbInstanceConnectionName() {
        return properties.getProperty("database.instanceconnectionname");
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

