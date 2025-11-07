
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

4. Navigate to the **foundation** directory

```bash
  cd crashmeyer/foundation
```
5. Create the project and assign it to an existing Billing ID
```bash
  ./1a-create-project.sh
```
