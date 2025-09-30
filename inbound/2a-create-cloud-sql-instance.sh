#!/bin/bash

DB_INSTANCE_NAME="crashmeyer-instance"
DB_NAME="acme"
DB_ROOT_PASSWORD=`gcloud secrets versions access latest --secret=db-password`


gcloud sql instances create $DB_INSTANCE_NAME \
	--database-version=POSTGRES_17 \
	--region=us-east5 \
	--cpu=2 \
	--memory=8GB \
	--edition=ENTERPRISE \
	--root-password="$DB_ROOT_PASSWORD"

gcloud sql databases create $DB_NAME --instance $DB_INSTANCE_NAME
