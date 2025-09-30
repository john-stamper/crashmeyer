#!/bin/bash

RANDOM_PWD=`LC_ALL=C tr -dc 'A-Za-z0-9!"#$%&'\''()*+,-./:;<=>?@[\]^_`{|}~' </dev/urandom | head -c 13; echo`

printf "$RANDOM_PWD" | gcloud secrets create db-password --data-file=-
