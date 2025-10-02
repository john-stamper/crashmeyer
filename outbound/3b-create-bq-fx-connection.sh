#!/bin/bash

PROJ_ID=`gcloud config list --format="value(core.project)"`

bq mk --connection --location=us --project_id=$PROJ_ID \
    --connection_type=CLOUD_RESOURCE remote_fx
