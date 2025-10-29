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
