# Car Adverts
This application is based on below technolgy stack:

- Scala
- Akka HTTP
- DynamoDB

For running the application, requires below port:

- Application is running on port **8080**
- DynamoDB is running on port **7000**
- In-Memory dynamoDB is running on port **7001** (For Test Case Only)

## Below are the commands for running and test the application

**Note: Before running application DynamoDB is up**

### Running the application
```
$ car-adverts> sbt clean compile run //running the application on port 8080
```

### Running Test Cases & Generate Coverage Reports

```
$ car-adverts> sbt clean test coverage //running all unit test with coverage
$ car-adverts> sbt coverageReport //create coverage report
```

### Generate Code Static Analysis Reports
```
$ car-adverts> sbt scapegoat
```

After Running the application, you can import `CarAdvert.postman_collection.json` file into user portman collection, and execute rest services requests.