#!/bin/bash

gcloud run deploy crashmeyer-bq-remotefx-decryptor \
	--gen2 \
	--source . \ 
	--runtime go124 \ 
	--entry-point AEADDecrypt \
	--region us-central1 \
	--allow-unauthenticated \
