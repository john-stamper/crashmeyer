#!/bin/bash

DISPLAY_NAME="crashmeyer"
SQL_INSTANCE_ID=`gcloud sql instances describe crashmeyer-instance --format="value(connectionName)"`
DB_PASSWORD=`gcloud secrets versions access latest --secret=db-password`
PROJECT_ID=`gcloud config list --format="value(core.project)"`

bq mk --connection --display_name="$DISPLAY_NAME" --connection_type='CLOUD_SQL' \
      --properties='{"instanceId":"'"$SQL_INSTANCE_ID"'","database":"acme","type":"POSTGRES"}' --connection_credential='{"username":"postgres","password":"'"$DB_PASSWORD"'"}' \
      --project_id="$PROJECT_ID" --location='us' \
      crashmeyer-acme-connection
