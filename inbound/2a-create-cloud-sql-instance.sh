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

DB_INSTANCE_NAME="crashmeyer-instance"
DB_NAME="acme"
DB_ROOT_PASSWORD=`gcloud secrets versions access latest --secret=db-password`
IP_ADDRESS=`curl ipinfo.io/ip`


gcloud sql instances create $DB_INSTANCE_NAME \
	--database-version=POSTGRES_17 \
	--region=us-central1 \
	--cpu=2 \
	--memory=8GB \
	--edition=ENTERPRISE \
	--root-password="$DB_ROOT_PASSWORD" \
	--authorized-networks=$IP_ADDRESS/32

gcloud sql databases create $DB_NAME --instance $DB_INSTANCE_NAME
SQL_INSTANCE_ID=`gcloud sql instances describe $DB_INSTANCE_NAME --format="value(connectionName)"`
echo "database.instanceconnectionname=$SQL_INSTANCE_ID" >> ../sql-app/src/main/resources/config.properties
