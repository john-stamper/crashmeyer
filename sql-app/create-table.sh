#!/bin/bash

export GOOGLE_APPLICATION_CREDENTIALS=`realpath ../sql-app/src/main/resources/sa-sqlclient-kms.json`

java -cp ./target/blog-1.0-SNAPSHOT.jar com.example.CreateTable
