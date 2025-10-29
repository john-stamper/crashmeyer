# Copyright 2024 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#!/bin/bash

DISPLAY_NAME="crashmeyer"
SQL_INSTANCE_ID=`gcloud sql instances describe crashmeyer-instance --format="value(connectionName)"`
DB_PASSWORD=`gcloud secrets versions access latest --secret=db-password`
PROJECT_ID=`gcloud config list --format="value(core.project)"`
PROJECT_NUMBER=`gcloud projects describe $PROJECT_ID --format="value(projectNumber)"`

bq mk --connection --display_name="$DISPLAY_NAME" --connection_type='CLOUD_SQL' \
      --properties='{"instanceId":"'"$SQL_INSTANCE_ID"'","database":"acme","type":"POSTGRES"}' --connection_credential='{"username":"postgres","password":"'"$DB_PASSWORD"'"}' \
      --project_id="$PROJECT_ID" --location='us' \
      crashmeyer-acme-connection

gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:service-$PROJECT_NUMBER@gcp-sa-bigqueryconnection.iam.gserviceaccount.com" \
  --role="roles/cloudsql.client" \
  --quiet
