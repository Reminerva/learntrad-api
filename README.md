# LearnTrad API Documentation

This document provides a introduction and comprehensive guide to using the LearnTrad API, including available endpoints, request/response formats, and setup instructions.

## LearnTrad API

LearnTrad API is a microservices-based backend system built to support a trading education platform. It provides services such as authentication, customer and admin management, market data handling, and trade placing and processing — all designed using Spring Boot and deployed using Kubernetes.

A key feature of this platform is the Quiz Fetching system, where customers receive anonymous, preprocessed time-series data in the form of candlestick charts. The data is intentionally obfuscated — its real asset name, time frame, and price scale are hidden or altered — to ensure users cannot look up actual historical prices while solving the quiz. This encourages independent analysis and strengthens data interpretation skills.

This system is developed for educational purposes, and serves as a scalable simulation of real-world trading services and challenges.

**App Architecture** :

**ERD** : 

## Table of Contents
1.  [LearnTrad Features](#1-learntrad-features)
2.  [API Documentation (Swagger UI)](#2-api-documentation-swagger-ui)
3.  [Getting Started](#3-getting-started)
    * [Prerequisites](#prerequisites)
    * [Recommended System Requirements](#recommended-system-requirements)
    * [Environment Setup](#environment-setup)
    * [Running the Application](#running-the-application)
        * [Option A: Running Locally using Batch Script](#option-a-running-locally-using-batch-script)
        * [Option B: Running with Kubernetes](#option-b-running-with-kubernetes)
    * [Postman Collection](#postman-collection)
4.  [Common Responses](#4-common-responses)
5.  [Roles and Authorization](#5-roles-and-authorization)
6.  [API Endpoints](#6-api-endpoints)
    * [API Gateway](#api-gateway)
    * [Authentication](#authentication)
    * [Customer Service](#customer-service)
    * [Market Data Service](#market-data-service)
    * [Trade Service](#trade-service)
    * [Top Up Service](#top-up-service)
    * [Market Realtime Service](#market-realtime-service)
    * [Trade Processor Service](#trade-processor-service)
    * [Notification Service](#notification-service)
7. [Models](#7-models)
    * [Request Models](#request-models)
        * [LoginRequest](#loginrequest)
        * [RegisterRequest](#registerrequest)
        * [RefreshTokenRequest](#refreshtokenrequest)
    * [Response Models](#response-models)

---

## 1. LearnTrad Features

### Secure by Design
Utilizing an API Gateway as the single entry point, all requests are filtered before being routed to the appropriate service. The gateway includes a circuit breaker (Resilience4j) to improve fault tolerance.
Authentication and authorization are handled by Keycloak, supporting OAuth 2.0 with RS256 and HS256 signature algorithms.
Token invalidation (logout) is managed via Redis, ensuring revoked tokens cannot be reused.

### Event-Driven Architecture
Several services use asynchronous communication via Kafka (with Zookeeper and Kafka UI enabled for monitoring), ensuring the system remains responsive under load.

### Powerful Monitoring Stack
Full observability is provided through the Grafana stack, including:

* Prometheus for metrics
* Tempo and Zipkin for distributed tracing
* Loki for centralized logging

### Rich Historical Market Data
Comprehensive XAUUSD data is available from 2004 to August 2025, with a minimum resolution of 1 minute. It is stored in TimescaleDB for efficient time-series querying.
Other business-related data such as customer profiles, top-ups, and trades are stored in PostgreSQL.

### Live Integration & Secure Access
The application integrates with the Twelve Data API to provide real-time market prices. Customers can access this data securely using access tokens, based on their authorization level.

### Customer Experience & Profile Management
Users can register, create and update their customer profiles, and retrieve their personal data securely.
Admins have full access to view all customer information.

### Trading Simulation
Customers can:
* Execute market orders and place pending orders (buy stop, buy limit, sell stop, sell limit)
* Modify existing trades and retrieve their own trade data
* All trades are processed and analyzed by the trade-processor-service to determine their status (running, profit, or loss)

### Top-Up System
Customers can top up their balance, receive dummy payment confirmations, and view their own top-up data.
Admins have access to all top-up records across the platform.

### Quiz Generation with Anonymized Market Data
One of LearnTrad’s core features is the Fetch Quiz, which delivers anonymized historical candlestick data:
* The entity (e.g., XAUUSD) is hidden
* The timeframe is undisclosed
* Price values are rescaled to prevent matching with public historical data
* The goal is to encourage customers to think independently without "cheating" by referencing actual historical data.

### Multi-Timeframe Data Access
Customers can access historical candlestick data across multiple timeframes:
1min, 5min, 15min, 30min, 1h, 2h, 4h, 6h, 8h, 12h, 1d, 3d, 1w, 1mo

---

## 2. API Documentation (Swagger UI)

You can explore the full API documentation, including all available endpoints, request models and parameters, and response models, directly through **Swagger UI**.

Once the backend application is running and the Api Gateway is exposed on port 9000, open your web browser and navigate to:

`http://localhost:9000/swagger-ui/index.html`

This interactive documentation allows you to:
* View detailed information about each endpoint.
* Try out API calls directly from the browser (though you'll need to manually add authorization tokens for protected endpoints).
* Understand the expected request and response formats.

---

## 3. Getting Started

You can run the microservices using one of the following options:

**Option A: Running Locally using Batch Script**
This method allows you to run the services from your local machine using `.bat` scripts (e.g., via VS Code terminal).

**Option B: Running with Kubernetes**
Instructions to deploy the services using Kubernetes and Docker.

We suggest you to run the app using the **Option B: Running with Kubernetes**.

---

### Prerequisites

Before running the application, make sure you have the following installed on your machine:

### General Requirements (applies to all options)
- **Java 21 or later** – Required to run Spring Boot services.
- **Git** – To clone the project repository.
- **Docker** – Required if running with Kubernetes or containerized services.

### Additional for **Option B: Running with Kubernetes**
- **kubectl** – Command-line tool for interacting with Kubernetes clusters.
- **Kind** – Local Kubernetes cluster for development.

---

### Recommended System Requirements

This project consists of multiple services running in separate Docker containers, which can be resource-intensive. For smooth local execution (especially when running all services together), we recommend the following minimum system specifications:

- **Processor**: AMD Ryzen 5 6600H with Radeon Graphics (3.30 GHz) or equivalent
- **RAM**: 16 GB
- **System Type**: 64-bit operating system, x64-based processor
- **OS**: Windows 11

⚠️ On machines with lower specs, the app might not run properly or may experience performance issues.
✅ These specs are based on the system used during testing, where the application was able to run successfully, although with some noticeable load on resources.

---

### Running the Application

You have two options to run the application: **Option A: Running Locally using Batch Script** or **Option B: Running with Kubernetes**.

---

#### Option A: Running Locally using Batch Script

⚠️ Before starting, make sure Docker is running properly on your machine.

1. **Clone the Repository**

    ```bash
    git clone -b development https://github.com/Reminerva/learntrad-api.git
    cd learntrad-api
    ```

2. **Open the Project**

    Open the root folder in Visual Studio Code or any preferred IDE/terminal.

3. **Setup Environment Variables**

    Each service has its own .env_example file located inside:

    ```bash
    <service-name>/src/main/resources/.env_example
    ```

    You need to:

    * Copy the file .env_example
    * Rename it to .env
    * Adjust variable values as needed
    * Repeat for each service.

    Example:

    ```bash
    copy customer-service\src\main\resources\.env_example customer-service\src\main\resources\.env
    ```

4. **Run the Docker Composes Batch Script**

    From the root project directory, run the batch script:

    ```bash
    ./run-all-docker-compose.bat
    ```

    This script will:
    * Build and run all necessary docker containers
    * Wait for each container to start
    * ⏳ The first run might take longer as images will be downloaded and containers will be built.

5. **Run the Services**

    From the root project directory, run the batch script:

    ```bash
    ./run-all-app.bat
    ```

    This script will:
    * Build and run all services
    * Wait for each service to start
    * ⏳ The first run might take longer as dependencies will be downloaded and services will be built.

6.  **Test the Endpoints**

    Once the services are running, visit their Swagger UIs (or Postman): `http://localhost:9000/swagger-ui/index.html`

7. **Stop the Application**

    To stop all services and clean up, run:

    ```bash
    ./stop-all.bat
    ```

    This will automatically terminate processes running on the relevant ports (e.g., 9000, 8081, etc.) and clean up the docker containers with the `docker compose down` command.

---

#### Option B: Running with Kubernetes

This guide will help you run the application using Kubernetes with Kind (Kubernetes IN Docker).
⚠️ Before starting, make sure Docker is running properly on your machine.

1. **Clone the Repository**

    ```bash
    git clone -b deployment https://github.com/Reminerva/learntrad-api.git
    cd learntrad-api
    ```

2. **Open the Project**

Open the project folder in Visual Studio Code or your terminal of choice.

3. **Setup Environment Variables**

You’ll need to configure environment variables before running the cluster:

* Navigate to the file:

    ```bash
    <root-project>/manifest/secrets-example.yml
    ```

* Rename the file to secrets.yml:
* Edit secrets.yml if you need to customize any environment variables.

4. **Create the Kind Cluster**

    Run the following script to create a local Kubernetes cluster using Kind:

    ```bash
    ./kind/create-kind-cluster.sh
    ```

    ⏳ This step might take a few minutes. Please wait until the cluster is fully created.

5. **Deploy Infrastructure Services**

    Once the Kind cluster is up, apply the infrastructure manifests:

    ```bash
    kubectl apply -f manifest/infrastructure
    ```

    This will deploy supporting tools like databases, kafka, grafana stack, keycloak, etc.
    ⏳ This step might take a few minutes too. Please wait until the infrastructure is fully deployed.

6. **Deploy Application Services**

    Next, deploy the main application services:

    ```bash
    kubectl apply -f manifest/applications
    ```

    This includes services such as api-gateway, auth-service, customer-service, etc.

    ⏳ This step might take a few minutes. Please wait until the services are fully deployed. You can check the status of each service using the following command:
    
    ```bash
     kubectl get pods.
    ```

    After the pods status are **RUNNING**, you can check whether the services are ready to be exposed by running the following command
    
    ```bash
    kubectl logs -f deployment/<service-name>
    ```

    After the services are fully deployed and the API Gateway is ready to be exposed, you can expose the api-gateway using the following command:

    ```bash
    kubectl port-forward deployment/api-gateway 9000:9000
    ```

    This will expose the api-gateway service on port 9000 so that you can access the app and the API Documentation Swagger UI on `http://localhost:9000/swagger-ui/index.html`

7. **Stopping the Application**

    To delete the entire Kind cluster and stop all services, run:

    ```bash
    ./kind/delete-kind-cluster.sh
    ```

## 4. Common Responses

All API responses are wrapped in a `CommonResponse` object with the following structure:

```json
{
    "code": 200, // HTTP status code
    "message": "Success message",
    "data": {}, // The actual response data (can be object or array)
    "paging": { // Only present for paginated responses
        "totalPages": 2,
        "totalElement": 12,
        "page": 1,
        "size": 10,
        "hasNext": true,
        "hasPrevious": false,
    }
}
```

Error responses will also follow this structure, but with a non-2xx code and an appropriate message.

---

## 5. Roles and Authorization
The API implements role-based access control using Spring Security's @PreAuthorize annotation. The following roles are defined:

**ADMIN:** Has full access to all API endpoints.
**CUSTOMER:** Has access to endpoints related to their own customer profile, trade and top up information. Customers can also access endpoints related to market data.

To access protected endpoints, users must include a valid JWT Access Token (JSON Web Token) in the Authorization header of their requests, prefixed with Bearer.

Example:
Authorization: Bearer <your_jwt_access_token>

---

## 6. API Endpoints

This section details all the available API endpoints. All successful responses will follow the `CommonResponse` structure.

<details>
<summary><h2>API Gateway</h2></summary>

### API Gateway
This is the main entry point for the API. It is responsible for routing requests to the appropriate service. The default port is 9000.

</details>

<details>
<summary><h2>Authentication</h2></summary>

### Authentication

**Auth Base Path:** `/api/auth`

* **`POST {Auth Base Path}/register` - Register a new user (Customer)**
    * **Description:** Allows a new customer to register an account.
    * **Roles Required:** Public
    * **Request Example (RegisterRequest):**
        ```json
        {
            "username": "johndoe123",
            "email": "john.doe@example.com",
            "password": "securepassword123"
        }
        ```
    * **Response Example:**
        ```json
        {
            "code": 201,
            "message": "Register success",
            "data": {},
            "paging": null
        }
        ```
* **`POST {Auth Base Path}/login` - User Login**
    * **Description:** Authenticates a user and returns a JWT token.
    * **Roles Required:** Public
    * **Request Example (LoginRequest):**
        ```json
        {
            "username": "john.doe@example.com",
            "password": "securepassword123"
        }
        ```
    * **Response Example:**
        ```json
        {
            "code": 202,
            "message": "Log in success",
            "data": {
                "accessToken": "access-token-example",
                "refreshToken": "refresh-token-example"
            },
            "paging": null
        }
        ```
* **`POST {Auth Base Path}/logout` - User Logout**
    * **Description:** Invalidates the user's session/token.
    * **Roles Required:** Authenticated Users (Admin, Cashier, Customer)
    * **Request Example:** (No request body needed, typically uses Bearer JWT Access Token in header)
        ```json
        {}
        ```
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Log out success",
            "data": {},
            "paging": null
        }
        ```
* **`POST {Auth Base Path}/refresh-token` - Refresh Token**
    * **Description:** Refreshes the user's JWT token.
    * **Roles Required:** Authenticated Users (Admin, Cashier, Customer)
    * **Request Example (RefreshTokenRequest):**
        ```json
        {
            "refreshToken": "refresh-token-example"
        }
        ```
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Refresh token success",
            "data": {
                "accessToken": "access-token-example",
                "refreshToken": "refresh-token-example"
            },
            "paging": null
        }
</details>

