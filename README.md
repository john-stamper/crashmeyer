Open the Google Cloud Console, launch a Cloud Shell, Git clone the repository, and follow the instructions below. Each step below corresponds to a directory with the appropriate software for needed resources.

## 1 - Foundation 
	Create a new project and link it to an open billing account
	Update the script to set the PROJ_OWNER_ACCOUNT variable to a User Principal with Owner permissions prior to running the script.
	Enable the required APIs - This script will take a minute to complete.
	Create a Secret Manager secret for the Cloud SQL password
	Create a KMS key for encryption and decryption
	Create an IAM Service Account with Cloud SQL Client permissions and access to the KMS key
## 2 - Inbound 
	Create a Cloud SQL Postgres instance and database - This script will take up to five minutes to complete.
## 3 - Outbound 
	Create a BigQuery-Cloud SQL Connection
	Create BigQuery Remote Function Connection
	Create BigQuery Remote Function
	
## Real-World Application

## Create the Client App JAR
	Navigate to the __sql-app__ directory and run the following command:
		```
		$ mvn package
		```
		This will compile and package the Java code into a jar file for the next steps

Create the sales table in the acme database

	./create-table.sh 

Load the sales table with mock sales data

	./load-table.sh

Open a new tab in the Cloud Shell, Tab-2
	(Tab-2) Copy the DB password from the ./sql-app/src/main/resources/config.properties file
	(Tab-2) Connect to the Cloud SQL instance with the user postgres and enter the password when prompted

		gcloud sql connect crashmeyer-instance –user postgres

		Connect to the acme database

		\c acme

		Run a query to see the encrypted fields

		select customer_id, full_name, email_address from sales limit 5;

Navigate to the Cloud Run Functions Console and copy the endpoint value of the remote function

Navigate to the BigQuery Console

Create a Dataset, sales_analysis in the US location

Create a BigQuery Function in the newly created dataset that references the Cloud Run Functions endpoint

	CREATE FUNCTION sales_analysis.aead_decrypt(x STRING, y STRING)
	RETURNS STRING
	REMOTE WITH CONNECTION us.remote_fx
	OPTIONS (
		endpoint = '[Cloud Run Function Endpoint]'
	)

Run a Sales Analysis query to view the top 10 customers by spend

select
	sales_analysis.aead_decrypt(TO_BASE64(t1.customer_id),t1.purchase_uuid) as decrypted_customer_id,                              
	sales_analysis.aead_decrypt(TO_BASE64(t1.full_name),t1.purchase_uuid) as decrypted_full_name,
	sales_analysis.aead_decrypt(TO_BASE64(t1.email_address),t1.purchase_uuid) as decrypted_email_address, t1.TotalSpent
from
 external_query('us.crashmeyer-acme-connection','''select purchase_uuid, customer_id, full_name, email_address, sum(total_price) as TotalSpent from sales group by purchase_uuid, customer_id, full_name, email_address order by TotalSpent desc limit 10''') as t1
