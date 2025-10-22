#!/bin/bash

gcloud services enable \
	cloudkms.googleapis.com \
	bigqueryconnection.googleapis.com \
	artifactregistry.googleapis.com \
	sqladmin.googleapis.com \
	compute.googleapis.com \
	run.googleapis.com \
	secretmanager.googleapis.com \
	cloudfunctions.googleapis.com \
	cloudbuild.googleapis.com

