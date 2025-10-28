#!/bin/bash

SERVICE_ACCOUNT_NAME="sql-client-kms"
PROJECT_ID=`gcloud config list --format="value(core.project)"`
KEY_FILE_NAME="sa-sqlclient-kms.json"
PROJECT_NUMBER=`gcloud projects describe $PROJECT_ID --format="value(projectNumber)"`
KEY_RING="crashmeyer"
KEY_NAME="symmetric-encrypt-decrypt"

gcloud iam service-accounts create $SERVICE_ACCOUNT_NAME \
  --description="A service account for Cloud SQL connections and access to KMS key" \
  --display-name="$SERVICE_ACCOUNT_NAME"

# SERVICE ACCOUNT USE IS EVENTUALLY CONSISTENT
sleep 10

SERVICE_ACCOUNT_EMAIL=`gcloud iam service-accounts list --filter="displayName:$SERVICE_ACCOUNT_NAME" --format="value(email)"`

gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:$SERVICE_ACCOUNT_EMAIL" \
  --role="roles/cloudsql.client" \
  --quiet

gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:$SERVICE_ACCOUNT_EMAIL" \
  --role="roles/editor" \
  --quiet

gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="$PROJECT_NUMBER-compute@developer.gserviceaccount.com" \
  --role="roles/cloudkms.cryptoKeyEncrypterDecrypter" \
  --quiet

gcloud kms keys add-iam-policy-binding $KEY_NAME \
    --location=global \
    --keyring=$KEY_RING \
    --member="serviceAccount:$SERVICE_ACCOUNT_EMAIL" \
    --role="roles/cloudkms.cryptoKeyEncrypterDecrypter"

gcloud iam service-accounts keys create ../sql-app/src/main/resources/$KEY_FILE_NAME \
    --iam-account="${SERVICE_ACCOUNT_EMAIL}"

export GOOGLE_APPLICATION_CREDENTIALS=`realpath ../sql-app/src/main/resources/$KEY_FILE_NAME`
