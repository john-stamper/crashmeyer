#!/bin/bash

KEY_RING="crashmeyer"
KEY_NAME="symmetric-encrypt-decrypt"

gcloud kms keyrings create $KEY_RING --location global
gcloud kms keys create $KEY_NAME \
        --keyring $KEY_RING \
        --location global \
        --purpose "encryption"

KMS_KEY_URI=`gcloud kms keys list --keyring $KEY_RING --location global --format="value(NAME)"`
echo "--set-env-vars=KMS_KEY_URI=gcp-kms://$KMS_KEY_URI" >> ../bq-remote-fx/deploy.sh
echo "kms.keyuri=gcp-kms://$KMS_KEY_URI" >> ../sql-app/src/main/resources/config.properties
