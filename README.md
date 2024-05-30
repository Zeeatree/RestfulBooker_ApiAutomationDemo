# Restful Booker API Test Automation

## Overview

This project is a comprehensive test automation framework for the Restful Booker API. It includes various types of tests such as smoke tests, regression tests, and schema validation tests. The framework is built using Java, JUnit 5, REST Assured, and Log4j2 for logging.

## Features

- **Data-Driven Testing**: Uses JSON and CSV files to drive test data.
- **Parameterized Tests**: Executes tests with multiple sets of data.
- **Schema Validation**: Ensures API responses adhere to predefined JSON schemas.
- **Advanced Logging**: Detailed logging for effective debugging and monitoring.
- **Test Suites**: Organized into smoke tests, regression tests, and integration tests.
- **Thread Safety**: Uses thread-local storage for managing test context.

## Project Structure

```
src
├── main
│   ├── java
│   │   └── com
│   │       └── restfulBooker
│   │           ├── config
│   │           │   └── TestConfig.java
│   │           ├── models
│   │           │   ├── Booking.java
│   │           │   ├── BookingDates.java
│   │           └── utilities
│   │               ├── BookingTestContext.java
│   │               ├── CsvUtil.java
│   │               ├── JsonFileUtil.java
│   │               ├── JsonUtil.java
│   │               └── SchemaValidator.java
├── test
│   ├── java
│   │   └── com
│   │       └── restfulBooker
│   │           ├── suites
│   │           │   ├── SmokeTestSuite.java
│   │           │   ├── RegressionTestSuite.java
│   │           │   └── IntegrationTestSuite.java
│   │           └── tests
│   │               ├── DataDrivenBookingTestsJson.java
│   │               ├── SchemaValidationTests.java
│   └── resources
│       ├── data
│       │   ├── bookings.json
│       │   └── bookings.csv
│       └── schemas
│           └── booking-schema.json
```

## Prerequisites

- Java 11 or higher
- Maven 3.6.3 or higher

## Setup

1. **Clone the repository**:

   ```sh
   git clone https://github.com/yourusername/restful-booker-api-tests.git
   cd restful-booker-api-tests
   ```

2. **Install dependencies**:

   ```sh
   mvn clean install
   ```

## Running Tests

### Running All Tests

To run all tests, use the following command:

```sh
mvn test
```

### Running Specific Test Suites

**Smoke Test Suite**:

```sh
mvn test -Dtest=com.restfulBooker.suites.SmokeTestSuite
```

**Regression Test Suite**:

```sh
mvn test -Dtest=com.restfulBooker.suites.RegressionTestSuite
```

**Integration Test Suite**:

```sh
mvn test -Dtest=com.restfulBooker.suites.IntegrationTestSuite
```

## Advanced Logging

The framework uses Log4j2 for logging. Logs are output to both the console and a file located at `logs/app.log`.

### Log Configuration

The log configuration is defined in `src/test/resources/log4j2.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"/>
        </Console>
        <File name="File" fileName="logs/app.log">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"/>
        </File>
    </Appenders>
    <Loggers>
        <Root level="debug">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="File"/>
        </Root>
    </Loggers>
</Configuration>
```

## Schema Validation

The framework includes schema validation to ensure API responses adhere to predefined JSON schemas. The schema is defined in `src/test/resources/schemas/booking-schema.json`.

### Example Schema

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "properties": {
    "bookingid": {
      "type": "integer"
    },
    "booking": {
      "type": "object",
      "properties": {
        "firstname": {
          "type": "string"
        },
        "lastname": {
          "type": "string"
        },
        "totalprice": {
          "type": "integer"
        },
        "depositpaid": {
          "type": "boolean"
        },
        "bookingdates": {
          "type": "object",
          "properties": {
            "checkin": {
              "type": "string",
              "format": "date"
            },
            "checkout": {
              "type": "string",
              "format": "date"
            }
          },
          "required": ["checkin", "checkout"]
        },
        "additionalneeds": {
          "type": "string"
        }
      },
      "required": ["firstname", "lastname", "totalprice", "depositpaid", "bookingdates"]
    }
  },
  "required": ["bookingid", "booking"]
}
```

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any improvements or bug fixes.


## Contact

For any questions or suggestions, please contact [zulpikar.alkam@torontomu.ca].

