Java Employee Challenge – API Module

Java Employee Challenge – API Module

    This repository contains the solution to the API module for the java-employee-challenge assignment. The implementation is completed as per the assignment requirements and added as a sub-module in the existing project structure.

What’s Done
A. API module implementation

    Implemented all required API endpoints for employee operations including create, get, delete, and custom queries like highest salary and top earners.

    Test logging integrated
    Configured testLogging in api/build.gradle. On running ./gradlew clean build, test results will be visible directly in the console and also available in an HTML report.

    Logging Configuration in application.yml

        Logging is enabled using Spring Boot's default logging framework.

        Logs are written to a file at the project root:

        logs/app.log

        Logging level is set for root and the main package:

       The mock API base URL is configured in the same file:

       
B. Postman Collection Added

    Included a Postman collection at the project root level. You can import it into Postman to test all API endpoints easily.

    Note: Some APIs may require you to manually enter a valid employee id in the URL.

C. Code Formatting

    Applied consistent code formatting using Spotless plugin via:

    ./gradlew spotlessApply

D. Steps to Run the Project
1. Clone the Repository

git clone https://github.com/vipinjain007/rq-assignment.git
cd rq-assignment/java-employee-challenge

2. Build the Project

./gradlew clean build

    This will compile all modules and run the tests.

    Console will display test results (pass/fail summary).

    A full HTML test report will be available at:
    ./api/build/reports/tests/test/index.html

3. Start the Mock Employee API (Server Module)

Run the server module to serve mock employee data:

./gradlew server:bootRun

    On Windows, use gradlew instead of ./gradlew if needed.

4. Start the API Module

This is the main Spring Boot API implementation:

./gradlew api:bootRun

E. Testing with Postman

    Import the Postman collection file: employee-api.postman_collection.json

    Update any required path parameters (like employee ID) in the request URLs.

   
F. Testing Notes

    Test results are visible in the console during build.

    HTML reports are available at:

   ./api/build/reports/tests/test/index.html

Spotless can be run before pushing code to apply formatting:

    ./gradlew spotlessApply

