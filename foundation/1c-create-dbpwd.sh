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

# --- Script to generate a random, Base64-encoded password ---

# 1. Generate 12 random bytes from /dev/urandom
# 2. Base64 encode those bytes (output will be 16 characters long, as (12 * 8) / 6 = 16)
# 3. Remove any trailing equals signs (=) that Base64 encoding sometimes adds
# 4. Print the result

echo "Generating a random 16-character Base64 password..."

RANDOM_PASSWORD=$(
  head /dev/urandom |       # Take the first part of the infinite random stream
  tr -dc A-Za-z0-9 |      # Keep only alphanumeric characters (optional, but good practice)
  head -c 12 |             # Select exactly 12 bytes
  base64 |                 # Base64 encode the 12 bytes (results in 16 chars)
  tr -d '='                # Remove any padding characters
)

echo "--------------------------------------------------------"
echo "Generated Password:"
echo "$RANDOM_PASSWORD"
echo "--------------------------------------------------------"

printf "$RANDOM_PASSWORD" | gcloud secrets create db-password --data-file=-
echo "database.password=$RANDOM_PASSWORD" >> ../sql-app/src/main/resources/config.properties
