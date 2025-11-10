
# Crashmeyer

Step by step instructions demonstrating how to access Cloud SQL envelope encrypted fields via BigQuery Federated Query




## Project Status

Crashmeyer is a MVP from field personnel and will be updated on an as-needed basis.
## Description

Crashmeyer is a step-by-step set of instructions to demonstrate how to insert records into a Cloud SQL instance database where PII fields are client-side encrypted using envelope encryption provided by Cloud KMS and subsequently execute a BigQuery Federated Query to decrypt the enveloped PII (by way of a BigQuery remote function) and view the PII.



## Authors

- [John Stamper](clowndaddy@google.com)


## Installation

1. Login the the [Google Cloud Console](https://console.cloud.google.com)

2. Launch the [Cloud Shell](https://cloud.google.com/shell/docs/using-cloud-shell) and click *Authorize* when prompted

3. Clone the crashmeyer Github repository. 

```bash
    git clone https://github.com/john-stamper/crashmeyer.git
```
### Establish the Foundation

4. Navigate to the **foundation** directory

```bash
  cd crashmeyer/foundation
```
5. Edit the 1a-create-project.sh file to set the PROJ_OWNER_ACCOUNT variable to a Principal with Owner privileges on the newly created project

6. Create the project and assign it to an existing Billing ID
```bash
  ./1a-create-project.sh
```
7. Enable the needed APIs for the demonstration
```bash
  ./1b-enable-apis.sh
```
8. Create a random database password for the Cloud SQL database
```bash
  ./1c-create-dbpwd.sh
```
9. Create the Cloud KMS encryption/decryption key
```bash
    ./1d-create-kms-key.sh
```
10. Create the service account for access to Cloud SQL and Cloud KMS
```bash
    ./1e-create-iam-svc-account-with-permissions.sh
```

### Create the Inbound service, a Cloud SQL instance and database
11. Navigate to the **inbound** directory
```bash
    cd ../inbound
```
12. Create a Cloud SQL instance and database
```bash
    ./2a-create-cloud-sql-instance.sh
```
### Create the Outbound capabilities
13. Navigate to the **outbound** directory
```bash
    cd ../outbound
```
14. Create the connection between BigQuery and the Cloud SQL database and allow the BigQuery default service account act as a SQL client
```bash
    ./3a-create-bq-sql-connection.sh
```
15. Create the connection between BigQuery and Remote Functions
```bash
    ./3b-create-bq-fx-connection.sh
```
16. Create the Remote Function in Cloud Run
```bash
    ./3c-create-bq-remote-function.sh
```
### Cloud SQL - create the table and load it with mock sales data using envelope encryption on PII fields
17. Navigate to the **sql-app** directory
```bash
    cd ../sql-app
```
18. Compile and package into a JAR file the client-side application
```bash
    mvn package
```
19. Create the table
```bash
    ./create-table.sh
```
20. Load the table with mock sales data using envelope encryption on PII fields
```bash
    ./load-table.sh
```
### View the encrypted PII fields
21. Copy the database password stored in the ./main/resources/config.properties file

22. Connect to the *acme* database and paste the password when prompted
```bash
    gcloud sql connect crashmeyer-instance --user postgres
```
23. Change to the *acme* database
```bash
    \c acme
```
24. Run a query to see the encrypted fields
```sql
select customer_id, full_name, email_address from sales limit 5;
```



