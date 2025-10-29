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
PROJECT_ID=`gcloud config list --format="value(core.project)"`
echo "project.id=$PROJECT_ID" >> ../sql-app/src/main/resources/config.properties
