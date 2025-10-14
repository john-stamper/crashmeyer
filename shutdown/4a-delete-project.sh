#!/bin/bash
$PROJECT_ID=`gcloud config list --format="value(core.project)"`

gcloud projects delete $PROJECT_ID
