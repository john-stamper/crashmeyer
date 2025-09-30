#!/bin/bash

KEY_RING="crashmeyer"
KEY_NAME="symmetric-encrypt-decrypt"

gcloud kms keyrings create $KEY_RING --location global
gcloud kms keys create $KEY_NAME \
	--keyring $KEY_RING \
	--location global \
	--purpose "encryption"
