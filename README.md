[![Build Status](https://travis-ci.org/Da-Bulgaria/e-prescriptions.svg?branch=master)](https://travis-ci.org/Da-Bulgaria/e-prescriptions)

### Environment Setup Requirements

##### Java

Please use OpenJDK version 11.02 (build 11.0.2+9),
which you can download from [here.](https://jdk.java.net/archive/)

##### Maven

Please consider using latest Maven version 3.6.3

### Build

//TODO

### Run Locally

//TODO

### Run Tests

//TODO

### Data imports

#### Pharmacy Registry

1) Check the db related settings in application.properties

2) Run the PharmacyServiceImportStartIT, which will directly use the Repository 
to insert into whatever datasource was configured.

3) Or use the data.sql script located in src/main/resources