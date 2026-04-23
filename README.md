# Smart Campus RESTful API (JAX-RS)

**Author:** Akshya Prakkash Vanitha  
**Student ID:** w2083025  

---

## API Overview

This project implements a RESTful Smart Campus API using JAX-RS (Jersey) and Apache Tomcat. The system manages campus rooms, sensors assigned to rooms, and historical sensor readings.
The API is designed using REST principles such as resource-based URIs, stateless communication and appropriate HTTP methods. All data is stored in-memory using Java collections (`LinkedHashMap` and `ArrayList`), as required by the coursework specification, ensuring simplicity and avoiding the use of external databases.

The key resources in the system are:
- **Rooms** – represent physical locations
- **Sensors** – linked to rooms and provide environmental data
- **Sensor Readings** – historical data recorded for each sensor

The API also includes:
- a discovery endpoint for navigation
- query parameter filtering
- sub-resource locators for nested data
- custom exception handling with HTTP status mapping
- request/response logging using JAX-RS filters

## Technology Stack
- Java
- JAX-RS (Jersey)
- Maven
- Apache Tomcat
- In-memory data structures (Map, List)

## Project Structure

The project is organised into the following packages:
- **config** – contains the application configuration (JAX-RS setup)
- **exception** – custom exception classes used in the API
- **filter** – request/response logging filter
- **mapper** – exception mappers for handling errors
- **model** – data models such as Room, Sensor and SensorReading
- **resource** – REST API endpoints
- **store** – in-memory data storage using collections

## Base URL
The API is accessed via:

http://localhost:8080/SmartCampusAPI-1.0-SNAPSHOT/api/v1


When deployed via NetBeans and Tomcat, the application may include a context path:

http://localhost:8080/SmartCampusAPI-1.0-SNAPSHOT/api/v1
This depends on the deployment configuration, but the API root remains `/api/v1`.


## How to Build and Run

### Prerequisites

- Java installed
- NetBeans IDE
- Apache Tomcat configured in NetBeans
- Maven dependencies enabled

### Steps

1. Open the project in NetBeans
2. Right-click the project → **Clean and Build**
3. Run the project using Apache Tomcat
4. Wait for successful deployment
5. Test endpoints using browser, Postman, or curl

## HTTP Status Codes Used

- **200 OK** – Successful retrieval
- **201 Created** – Resource successfully created
- **404 Not Found** – Resource does not exist
- **409 Conflict** – Invalid operation (e.g. deleting room with sensors)
- **422 Unprocessable Entity** – Invalid reference (e.g. sensor linked to non-existent room)
- **415 Unsupported Media Type** – Invalid request format
- **500 Internal Server Error** – Unexpected failures

## Sample curl Commands

### Discovery Endpoint
```bash
curl -X GET http://localhost:8080/SmartCampusAPI-1.0-SNAPSHOT/api/v1
```
Get All Rooms
```bash
curl -X GET http://localhost:8080/SmartCampusAPI-1.0-SNAPSHOT/api/v1/rooms
```
Create Room
```bash
curl -X POST http://localhost:8080/SmartCampusAPI-1.0-SNAPSHOT/api/v1/rooms \
-H "Content-Type: application/json" \
-d "{\"id\":\"ART-201\",\"name\":\"Art Studio\",\"capacity\":25}"
```
Get One Room
```bash
curl -X GET http://localhost:8080/SmartCampusAPI-1.0-SNAPSHOT/api/v1/rooms/ART-201
```
Delete Room
```bash
curl -X DELETE http://localhost:8080/SmartCampusAPI-1.0-SNAPSHOT/api/v1/rooms/ART-201
```
Get Sensors
```bash
curl -X GET http://localhost:8080/SmartCampusAPI-1.0-SNAPSHOT/api/v1/sensors
```
Filter Sensors
```bash
curl -X GET "http://localhost:8080/SmartCampusAPI-1.0-SNAPSHOT/api/v1/sensors?type=CO2"
```
Create Sensor
```bash
curl -X POST http://localhost:8080/SmartCampusAPI-1.0-SNAPSHOT/api/v1/sensors \
-H "Content-Type: application/json" \
-d "{\"id\":\"CO2-101\",\"type\":\"CO2\",\"status\":\"ACTIVE\",\"currentValue\":415.0,\"roomId\":\"ART-201\"}"
```
Update Sensor Status
```bash
curl -X PUT http://localhost:8080/SmartCampusAPI-1.0-SNAPSHOT/api/v1/sensors/CO2-101/status \
-H "Content-Type: application/json" \
-d "\"MAINTENANCE\""
```
Get Sensor Readings
```bash
curl -X GET http://localhost:8080/SmartCampusAPI-1.0-SNAPSHOT/api/v1/sensors/CO2-101/readings
```
Add Sensor Reading
```bash
curl -X POST http://localhost:8080/SmartCampusAPI-1.0-SNAPSHOT/api/v1/sensors/CO2-101/readings \
-H "Content-Type: application/json" \
-d "{\"id\":\"R-001\",\"timestamp\":1713950000000,\"value\":420.5}"
```

---
## Report Answers
Part 1: Service Architecture & Setup
1.1 Resource Lifecycle
In JAX-RS, resource classes are typically instantiated per request. This means a new object is created for each incoming HTTP request, ensuring that instance variables are not shared across requests.
To maintain persistent application data, this implementation uses static collections in the CampusStore class. These collections store rooms, sensors, and sensor readings, ensuring that data is preserved across multiple requests.
Since these collections are shared across threads, there is a potential risk of race conditions in a real-world system. However, for the purposes of this coursework, using standard collections is sufficient. In production systems, thread-safe structures or synchronization mechanisms would be required.

1.2 Hypermedia (HATEOAS)
Hypermedia enables clients to dynamically navigate an API using links provided in responses. This reduces dependency on static documentation and improves flexibility.
In this API, the discovery endpoint provides entry-level navigation information, allowing clients to understand available resources. This approach improves maintainability and supports extensibility, as clients do not need to hardcode endpoint structures.

Part 2: Room Management
2.1 Returning IDs vs Full Objects
Returning only IDs reduces payload size and improves performance, especially for large datasets. However, it requires additional requests from the client to retrieve full details.
Returning full objects increases response size but simplifies client-side processing. In this implementation, full objects are returned to prioritise usability and clarity.

2.2 DELETE Idempotency
The DELETE operation is idempotent. Once a room is deleted, repeating the same DELETE request does not change the system state further.
If the room no longer exists, a 404 response is returned. If the room still has sensors linked, a 409 conflict is returned. In all cases, repeated requests do not introduce new side effects.

Part 3: Sensor Operations & Linking
3.1 @Consumes Behaviour
The API explicitly consumes JSON using @Consumes(MediaType.APPLICATION_JSON). If a client sends data in another format, such as XML or plain text, the request is rejected with a 415 Unsupported Media Type response.
3.2 Query Parameters vs Path Parameters
Query parameters are used for filtering because they provide flexibility. For example:
/api/v1/sensors?type=CO2
This keeps the endpoint structure consistent while allowing optional filters. Path parameters are more rigid and do not scale well for multiple filtering conditions.

### 3.3 PUT Idempotency
The PUT operation used for updating sensor status is idempotent. Repeating the same request results in the same final state, which aligns with REST principles.

Part 4: Sub-Resources and Data Management
4.1 Sub-Resource Locator Pattern
The API uses a sub-resource locator to manage sensor readings:
/sensors/{sensorId}/readings
This delegates responsibility to a separate class (SensorReadingResource), improving modularity and maintainability. It avoids creating a large, complex controller and keeps responsibilities clearly separated.

4.2 Historical Data Management
When a new sensor reading is added, it is stored in the sensor’s reading history and also updates the sensor’s current value.
This ensures consistency between historical data and the latest sensor state. Without this, the system could return outdated values.

Part 5: Error Handling & Logging
5.1 HTTP 422 vs 404
HTTP 422 is used when a request is syntactically valid but contains invalid data. For example, linking a sensor to a non-existent room.
This is more accurate than 404, which indicates that the resource itself could not be found.

5.2 Security Risks of Stack Traces
Exposing stack traces reveals internal implementation details such as class names and file structures. This information can be exploited by attackers.
To prevent this, a global exception mapper is used to return generic error responses instead of exposing internal errors.

5.3 Logging with Filters
Logging is implemented using JAX-RS filters instead of manually adding logging statements in each method.
This centralises logging logic, reduces code duplication, and ensures consistent logging across all API endpoints.

## Summary
This Smart Campus API demonstrates a complete RESTful design using JAX-RS, including:
-	structured resource design 
-	filtering with query parameters 
-	nested sub-resources 
-	proper HTTP status handling 
-	exception mapping 
-	logging via filters 
-	in-memory data storage 
