#!/bin/bash

PROJ_ID=crashmeyer
PROJ_NAME="CRASHMEYER"

# CHANGE THE PROJECT OWNER ACCOUNT VALUE TO A USER WHO HAS OWNER PRIVILEGES TO CREATE PROJECTS
# AND RESOURCES

PROJ_OWNER_ACCOUNT="CHANGE_ME"
#


BILLING_ACCOUNT_ID=`gcloud billing accounts list --filter=open=true --format="value(ACCOUNT_ID)"`

gcloud projects create $PROJ_ID --name $PROJ_NAME --set-as-default
gcloud config set account $PROJ_OWNER_ACCOUNT
gcloud billing projects link $PROJ_ID --billing-account=$BILLING_ACCOUNT_ID
