#!/bin/bash

export GOOGLE_APPLICATION_CREDENTIALS=`realpath ../sql-app/src/main/resources/$KEY_FILE_NAME`

java -cp ./target/blog*.jar com.example.CreateTable
