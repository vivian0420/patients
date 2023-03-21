# Patients Application

## To Build
*This project requires JDK17 and Apache Maven*
```shell
mvn package
```

## To import the Test Data CSV File

### Setup DB Schemas in MySQL
*Please make sure MySQL is running on your localhost and there is no existing schema called `patients`.*

Import `patients.sql` into your local MySQL database. Please note that

* `patients.sql` creates a DB schema called `patients` and a table called `patients` in your local MySQL database.
* It also creates a user called `application` with password `Welcome1` to run applications such as importing CSV file and the CRUD application.

### To Import the CSV Data
```shell
java -DdbUrl=jdbc:mysql://localhost:3306/patients -DdbUsername=application -DdbPassword=Welcome1 -jar ./target/patients-csv-parser.jar Intern_Test_Data.csv
```

### To Run the CRUD Application
Start the embedded Jetty server on localhost:8080
```shell
java -DdbUrl=jdbc:mysql://localhost:3306/patients -DdbUsername=application -DdbPassword=Welcome1 -DserverPort=8080 -jar ./target/patients-jetty-server.jar
```

### REST Endpoints
* Get a patient's record by `id`, **GET** `/patient`
```shell
curl -X GET "http://localhost:8080/patient?id=1"
```

* Create a new record, **POST** `/patient`
```shell
curl -X POST "http://localhost:8080/patient" -d '{"patient": "Firstname Lastname", "dob": "2000-01-02", "encounterDate": "2022-04-20", "providerNpi": 4}'
```

* Update an existing record, **PUT** `/patient`
```shell
curl -X PUT "http://localhost:8080/patient" -d '{"id": 72, "patient": "Firstname Lastname", "dob": "2000-01-02", "encounterDate": "2022-04-20", "providerNpi": 4}'
```
Please note that `id` must be provided for update (PUT).

* Delete an existing record, **DELETE** `/patient`
```shell
curl -X DELETE "http://localhost:8080/patient?id=32"
```

* List records, **GET** `/patients`
```shell
curl "http://localhost:8080/patients"
```

#### Paging ####
The CRUD application supports paging. Similar to MySQL `limits`, user can do paging through the `from` and `limits` query parameters:

To get the first 10 records
```shell
curl "http://localhost:8080/patients?from=0&limits=10"
```
To get the next 10 records
```shell
curl "http://localhost:8080/patients?from=10&limits=10"
```