#!/bin/bash

PROJ_ID=crashmeyer
PROJ_NAME="CRASHMEYER"
PROJ_OWNER_ACCOUNT="stamper.john@gmail.com"
BILLING_ACCOUNT_ID=`gcloud billing accounts list --filter=open=true --format="value(ACCOUNT_ID)"`

gcloud projects create $PROJ_ID --name $PROJ_NAME --set-as-default
gcloud config set account $PROJ_OWNER_ACCOUNT
gcloud billing projects link $PROJ_ID --billing-account=$BILLING_ACCOUNT_ID
