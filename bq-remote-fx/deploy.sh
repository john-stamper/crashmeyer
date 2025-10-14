#!/bin/bash

gcloud functions deploy crashmeyer-bq-remotefx-decryptor \
	--gen2 \
	--runtime go124 \
	--source . \
	--region us-central1 \
	--entry-point AEADDecrypt \
	--trigger-http \
	--allow-unauthenticated \
