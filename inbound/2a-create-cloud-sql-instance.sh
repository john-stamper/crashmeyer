#!/bin/bash

DB_INSTANCE_NAME="crashmeyer-instance"
DB_NAME="acme"
DB_ROOT_PASSWORD=`gcloud secrets versions access latest --secret=db-password`
IP_ADDRESS=`curl ipinfo.io/ip`


gcloud sql instances create $DB_INSTANCE_NAME \
	--database-version=POSTGRES_17 \
	--region=us-central1 \
	--cpu=2 \
	--memory=8GB \
	--edition=ENTERPRISE \
	--root-password="$DB_ROOT_PASSWORD" \
	--authorized-networks=$IP_ADDRESS/32

gcloud sql databases create $DB_NAME --instance $DB_INSTANCE_NAME
SQL_INSTANCE_ID=`gcloud sql instances describe $DB_INSTANCE_NAME --format="value(connectionName)"`
echo "database.instanceconnectionname=$SQL_INSTANCE_ID" >> ../sql-app/src/main/resources/config.properties
