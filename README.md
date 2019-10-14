# store
Capstone project of the course "Web Development with Java and Spring" at Grid Dynamics

## How to run

### Build all modules:

`./gradlew clean build`

### Start application in local:

`./gradlew bootRun`

## How to test project scenarios

To check project API you can use either Postman, Swagger or integration tests  

### Check API with Postman

There is a Postman collection "ngolovin-store.postman_collection.json" in the project root directory, 
which you can import to test project API

### Check API with Swagger 

Swagger is connected to the project and you can test project API using the link below

`http://localhost:8080/swagger-ui.html`

### Check API with integration tests

Integration tests are located in the ./src/test/java directory and you can run them with the command below

`./gradlew test`