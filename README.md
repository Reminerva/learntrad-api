# LearnTrad API Documentation

This document provides a introduction and comprehensive guide to using the LearnTrad API, including available endpoints, request/response formats, and setup instructions.

## LearnTrad API

LearnTrad API is a microservices-based backend system built to support a trading education platform. It provides services such as authentication, customer and admin management, market data handling, and trade placing and processing — all designed using Spring Boot and deployed using Kubernetes.

A key feature of this platform is the Quiz Fetching system, where customers receive anonymous, preprocessed time-series data in the form of candlestick charts. The data is intentionally obfuscated — its real asset name, time frame, and price scale are hidden or altered — to ensure users cannot look up actual historical prices while solving the quiz. This encourages independent analysis and strengthens data interpretation skills.

This system is developed for educational purposes, and serves as a scalable simulation of real-world trading services and challenges.

**App Architecture** : [**Lucid.app**](https://lucid.app/lucidspark/a21cf1a0-0350-4597-a55b-e923e33a341f/edit?viewport_loc=-814%2C-322%2C3709%2C2024%2C0_0&invitationId=inv_9565557f-f02d-4240-842e-333418859bd4)

**ERD** : 

## Table of Contents
1.  [**LearnTrad Features**](#1-learntrad-features)
    * [**Secure by Design**](#secure-by-design)
    * [**Event-Driven Architecture**](#event-driven-architecture)
    * [**Powerful Monitoring Stack**](#powerful-monitoring-stack)
    * [**Rich Historical Market Data**](#rich-historical-market-data)
    * [**Live Integration and Secure Access**](#live-integration-and-secure-access)
    * [**Customer Experience and Profile Management**](#customer-experience-and-profile-management)
    * [**Trading Simulation**](#trading-simulation)
    * [**Top-Up System**](#top-up-system)
    * [**Quiz Generation with Anonymized Market Data**](#quiz-generation-with-anonymized-market-data)
    * [**Multi-Timeframe Data Access**](#multi-timeframe-data-access)
2.  [**API Documentation (Swagger UI)**](#2-api-documentation-swagger-ui)
3.  [**Getting Started**](#3-getting-started)
    * [**Prerequisites**](#prerequisites)
    * [**Recommended System Requirements**](#recommended-system-requirements)
    * [**Environment Setup**](#environment-setup)
    * [**Running the Application**](#running-the-application)
        * [**Option A: Running Locally using Batch Script**](#option-a-running-locally-using-batch-script)
        * [**Option B: Running with Kubernetes**](#option-b-running-with-kubernetes)
    * [**Postman Collection**](#postman-collection)
4.  [**Common Responses**](#4-common-responses)
5.  [**Roles and Authorization**](#5-roles-and-authorization)
6.  [**API Endpoints**](#6-api-endpoints)
    * [**API Gateway**](#api-gateway)
    * [**Auth Service**](#auth-service)
    * [**Customer Service**](#customer-service)
    * [**Market Data Service**](#market-data-service)
    * [**Quiz Service**](#quiz-service)
    * [**Trade Service**](#trade-service)
    * [**Top Up Service**](#top-up-service)
7. [**Models**](#7-models)
    * [**Auth Service Model**](#auth-service-model)
        * [**Auth Service Request**](#auth-service-request)
            * [**LoginRequest**](#loginrequest)
            * [**RegisterRequest**](#registerrequest)
            * [**RefreshTokenRequest**](#refreshtokenrequest)
        * [**Auth Service Response**](#auth-service-response)
            * [**TokenResponse**](#tokenresponse)
    * [**Customer Service Model**](#customer-service-model)
        * [**Customer Service Request**](#customer-service-request)
            * [**CustomerRequest**](#customerrequest)
        * [**Customer Service Response**](#customer-service-response)
            * [**CustomerResponse**](#customerresponse)
    * [**Market Data Service Model**](#market-data-service-model)
        * [**Market Data Service Request**](#market-data-service-request)
            * [**QuizRequest**](#quizrequest)
            * [**AnswerRequest**](#answerrequest)
        * [**Market Data Service Response**](#market-data-service-response)
            * [**MarketDataResponse**](#marketdataresponse)
            * [**QuizResponse**](#quizresponse)
            * [**AnswerResponse**](#answerresponse)
    * [**Trade Service Model**](#trade-service-model)
        * [**Trade Service Request**](#trade-service-request)
            * [**TradeRequest**](#traderequest)
        * [**Trade Service Response**](#trade-service-response)
            * [**TradeResponse**](#traderesponse)
    * [**Top Up Service Model**](#top-up-service-model)
        * [**Top Up Service Request**](#top-up-service-request)
            * [**TopUpRequest**](#topuprequest)
        * [**Top Up Service Response**](#top-up-service-response)
            * [**TopUpResponse**](#topupresponse)

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

### Live Integration and Secure Access
The application integrates with the Twelve Data API to provide real-time market prices. Customers have the ability to access this data securely using access tokens, based on their authorization level.

### Customer Experience and Profile Management
Users can register, create and update their customer profiles, and retrieve their personal data securely.
Admins have full access to view all customer information.

### Trading Simulation
Customers are able to:
* Execute market orders and place pending orders (buy stop, buy limit, sell stop, sell limit)
* Modify existing trades and retrieve their own trade data
* All trades are processed and analyzed by the trade-processor-service to determine their status (running, profit, or loss)

### Top-Up System
Users may top up their balance, receive dummy payment confirmations, and view their own top-up data.
Admins have access to all top-up records across the platform.

### Quiz Generation with Anonymized Market Data
One of LearnTrad’s core features is the Fetch Quiz, which delivers anonymized historical candlestick data:
* The entity (e.g., XAUUSD) is hidden
* The timeframe is undisclosed
* Price values are rescaled to prevent matching with public historical data
* The goal is to encourage customers to think independently without "cheating" by referencing actual historical data.

### Multi-Timeframe Data Access
This feature allows customers to access historical candlestick data across multiple timeframes:
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
- **Twelve Data API key** – Used for fetching financial time series data in the marketdata-service. Visit [https://twelvedata.com/](https://twelvedata.com/) create account and get the API key.
- **Mailtrap** – For capturing outbound emails in a safe testing environment (used in notification-service). Visit [https://mailtrap.io/](https://mailtrap.io/).


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

4. **Run the Packages Installer Batch Script**

    From the root project directory, run the batch script:

    ```bash
    ./install-shared-packages.bat
    ```

    This script will:
    * Build and run all necessary maven packages
    * Wait for each package to finish building

5. **Run the Docker Composes Batch Script**

    From the root project directory, run the batch script:

    ```bash
    ./run-all-docker-compose.bat
    ```

    This script will:
    * Build and run all necessary docker containers
    * Wait for each container to start
    * ⏳ The first run might take longer as images will be downloaded and containers will be built.

6. **Run the Services**

    From the root project directory, run the batch script:

    ```bash
    ./run-all-app.bat
    ```

    This script will:
    * Build and run all services
    * Wait for each service to start
    * ⏳ The first run might take longer as dependencies will be downloaded and services will be built.

7.  **Test the Endpoints**

    Once the services are running, visit their Swagger UIs (or Postman): `http://localhost:9000/swagger-ui/index.html`

8. **Stop the Application**

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
    "data": {}, // The actual response data structure depends on the endpoint
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

Response data structures will be detailed in the following sections [Models](#7-models).

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

This section details all the available API endpoints. All responses will follow and wrapped in the [`CommonResponse`](#4-common-responses) structure. You can see the structure of the responses in the [Models](#7-models) section.

<details>
<summary><h2>API Gateway</h2></summary>

### API Gateway
This is the main entry point for the API. It is responsible for routing requests to the appropriate service. The default port is 9000.

</details>

<details>
<summary><h2>Auth Service</h2></summary>

### Auth Service

**Auth Base Path:** `/api/auth`

* **`POST {Auth Base Path}/register` - Register a new user (Customer)**
    * **Description:** Allows a new customer to register an account.
    * **Roles Required:** Public
    * **Request: (RegisterRequest [(See below)](#registerrequest))**
    * **Data Response: null**
* **`POST {Auth Base Path}/login` - User Login**
    * **Description:** Authenticates a user and returns a JWT token.
    * **Roles Required:** Public
    * **Request: (LoginRequest [(See below)](#loginrequest))**
    * **Data Response: (TokenResponse [(See below)](#tokenresponse))**
* **`POST {Auth Base Path}/logout` - User Logout**
    * **Description:** Invalidates the user's session/token.
    * **Roles Required:** Authenticated Users (Admin, Customer)
    * **Request:** (No request body needed, typically uses Bearer JWT Access Token in header)
    * **Data Response: null**
* **`POST {Auth Base Path}/refresh-token` - Refresh Token**
    * **Description:** Refreshes the user's JWT token.
    * **Roles Required:** Authenticated Users (Admin, Customer)
    * **Request: (RefreshTokenRequest [(See below)](#refreshtokenrequest))**
    * **Data Response: (TokenResponse [(See below)](#tokenresponse))**
* **`POST {Auth Base Path}/validate-token` - Validate Token**
    * **Description:** Validates the user's JWT token.
    * **Roles Required:** public
    * **Request:** (No request body needed, typically uses Bearer JWT Access Token in header)
    * **Data Response: (Boolean Type)**
</details>

<details>
<summary><h2>Customer Service</h2></summary>

### Customer Service

**Customer Base Path:** `/api/customer`

* **`POST {Customer Base Path}` - Create a new customer**
    * **Description:** Creates a new customer.
    * **Roles Required:** Customer
    * **Request: (CustomerRequest [(See below)](#customerrequest))**
    * **Data Response: (CustomerResponse [(See below)](#customerresponse))**
* **`GET {Customer Base Path}` - Get all customers**
    * **Description:** Retrieves a list of all customers with optional filtering, sorting, and pagination.
    * **Roles Required:** Admin
    * **Request:** (No request body needed)
    * **Data Response: List of (CustomerResponse [(See below)](#customerresponse))**
* **`GET {Customer Base Path}/{id}` - Get a specific customer by ID**
    * **Description:** Retrieves a specific customer by their ID.
    * **Roles Required:** Admin
    * **Request:** (No request body needed)
    * **Data Response: (CustomerResponse [(See below)](#customerresponse))**
* **`PUT {Customer Base Path}/{id}` - Update a specific customer by ID**
    * **Description:** Updates a specific customer by their ID.
    * **Roles Required:** Admin
    * **Request:** (No request body needed)
    * **Data Response: (CustomerResponse [(See below)](#customerresponse))**
* **`DELETE {Customer Base Path}/{id}` - Delete a specific customer by ID**
    * **Description:** Deletes a specific customer by their ID.
    * **Roles Required:** Admin
    * **Request:** (No request body needed)
    * **Data Response: null**
* **`GET {Customer Base Path}/me` - Get your own customer profile**
    * **Description:** Retrieves your own customer profile.
    * **Roles Required:** Customer
    * **Request:** (No request body needed)
    * **Data Response: (CustomerResponse [(See below)](#customerresponse))**
* **`PUT {Customer Base Path}/me` - Update your own customer profile**
    * **Description:** Updates your own customer profile.
    * **Roles Required:** Customer
    * **Request:** (No request body needed)
    * **Data Response: (CustomerResponse [(See below)](#customerresponse))**
* **`DELETE {Customer Base Path}/me` - Delete your own customer profile**
    * **Description:** Deletes your own customer profile.
    * **Roles Required:** Customer
    * **Request:** (No request body needed)
    * **Data Response: null**
</details>

<details>
<summary><h2>Market Data Service</h2></summary>

### Market Data Service

**Market Data Base Path:** `/api/market-data`

* **`GET {Market Data Base Path}/{marketDataType}/tick` - Get latest market data tick**
    * **Description:** Retrieves the latest market data tick.
    * **Roles Required:** Customer or Admin
    * **Request:** (No request body needed)
    * **Data Response: List of (MarketDataResponse [(See below)](#marketdataresponse))**
* **`GET {Market Data Base Path}/{marketDataType}` - Fetch a specific market data**
    * **Description:** Retrieves a specific market data.
    * **Roles Required:** Customer or Admin
    * **Request:** (No request body needed)
    * **Request Parameters (Optional):**
        - `timeBucketStartMin`: The minimum time bucket start (e.g., 2022-09-27T18:00:00Z).
        - `timeBucketStartMax`: The maximum time bucket start (e.g., 2022-09-27T18:00:00Z).
        - `timeFrame`: 1M, 5M, 15M, 1H, 2H, 4H, 6H, 8H, 12H, 1D, 3D, 1W, 1Mo.
        - `direction`: ASC or DESC.
    * **Data Response: (MarketDataResponse [(See below)](#marketdataresponse))**
* **`GET {Market Data Base Path}/{marketDataType}/{timeBucketStart}` - Fetch Market Data by specific time bucket start**
    * **Description:** Retrieves market data for a specific time bucket start.
    * **Roles Required:** Customer or Admin
    * **Request:** (No request body needed)
    * **Data Response: (MarketDataResponse [(See below)](#marketdataresponse))**

</details>

<details>
<summary><h2>Quiz Service</h2></summary>

### Quiz Service

**Quiz Base Path:** `/api/market-data/quiz`

* **`GET {Quiz Base Path}` - Get all quizzes**
    * **Description:** Retrieves a list of all quizzes.
    * **Roles Required:** Admin
    * **Request:** (No request body needed)
    * **Data Response: List of (QuizResponse [(See below)](#quizresponse))**
* **`GET {Quiz Base Path}/{id}` - Get a specific quiz by ID**
    * **Description:** Retrieves a specific quiz by their ID.
    * **Roles Required:** Admin
    * **Request:** (No request body needed)
    * **Data Response: (QuizResponse [(See below)](#quizresponse))**
* **`GET {Quiz Base Path}/mine` - Get your own quizzes**
    * **Description:** Retrieves your own quizzes.
    * **Roles Required:** Customer
    * **Request:** (No request body needed)
    * **Data Response: List of (QuizResponse [(See below)](#quizresponse))**
* **`POST {Quiz Base Path}/generate` - Generate a new quiz**
    * **Description:** Generates a new quiz.
    * **Roles Required:** Customer or Admin
    * **Request: (QuizRequest [(See below)](#quizrequest))**
    * **Data Response: (QuizResponse [(See below)](#quizresponse))**
* **`PUT {Quiz Base Path}/mine/{id}/answer` - Answer a specific quiz by ID**
    * **Description:** Answers a specific quiz by their ID.
    * **Roles Required:** Customer
    * **Request: (AnswerRequest [(See below)](#answerrequest))**
    * **Data Response: (QuizResponse [(See below)](#quizresponse))**

</details>

<details>
<summary><h2>Trade Service</h2></summary>

### Trade Service

**Trade Base Path:** `/api/trade`

* **`POST {Trade Base Path}` - Place a new trade**
    * **Description:** Creates a new trade.
    * **Roles Required:** Customer
    * **Request: (TradeRequest [(See below)](#traderequest))**
    * **Data Response: (TradeResponse [(See below)](#traderesponse))**
* **`POST {Trade Base Path}/market-execute` - Place a market execution trade**
    * **Description:** Creates a market execution trade.
    * **Roles Required:** Customer
    * **Request: (TradeRequest [(See below)](#traderequest))**
    * **Data Response: (TradeResponse [(See below)](#traderesponse))**
* **`GET {Trade Base Path}` - Get all trades**
    * **Description:** Returns a list of all trades with optional filtering, sorting, and pagination.
    * **Roles Required:** Admin
    * **Request:** (No request body needed)
    * **Request Parameters: (Optional)**
        - `id`: filter by specific trade ID,
        - `userId`: filter by specific user ID,
        - `lotMin`: filter by the lowe bound of the lot size,
        - `lotMax`: filter by the upper bound of the lot size,
        - `priceAtMin`: filter by the price at (Lower Bound. e.g., 450.00),
        - `priceAtMax`: filter by the price at (Upper Bound. e.g., 460.00),
        - `stopLossAtMin`: filter by the stop loss at (Lower Bound. e.g., 450.00),
        - `stopLossAtMax`: filter by the stop loss at (Upper Bound. e.g., 460.00),
        - `takeProfitAtMin`: filter by the take profit at (Lower Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `takeProfitAtMax`: filter by the take profit at (Upper Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `createdAtMin`: filter by the date of creating the trade (Lower Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `createdAtMax`: filter by the date of creating the trade (Upper Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `updatedAtMin`: filter by the date of updating the trade (Lower Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `updatedAtMax`: filter by the date of updating the trade (Upper Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `tradeAtMin`: filter by the trade at (Lower Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `tradeAtMax`: filter by the trade at (Upper Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `marketDataType`: filter by market data type (e.g., XAUUSD),
        - `tradeStatus`: filter by trade status (e.g., PENDING, RUNNING, PROFIT, LOSS, CANCELLED, EXPIRED),
        - `tradeType`:  filter by trade type,
        - `closedAtMin`: filter by the date of closing the trade (Lower Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `closedAtMax`: filter by the date of closing the trade (Upper Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `page`: page number (starting from 0),
        - `size`: number of items per page,
        - `sortBy`: field to sort by (e.g., createdAt, updatedAt, etc),
        - `direction`: sort direction (e.g., ASC, DESC)
    * **Data Response: List of (TradeResponse [(See below)](#traderesponse))**
* **`GET {Trade Base Path}/mine` - Get your own trades**
    * **Description:** Retrieves your own trades with optional filtering, sorting, and pagination.
    * **Roles Required:** Customer
    * **Request:** (No request body needed)
    * **Request Parameters: (Optional)**
        - `id`: filter by specific trade ID,
        - `lotMin`: filter by the lowe bound of the lot size,
        - `lotMax`: filter by the upper bound of the lot size,
        - `priceAtMin`: filter by the price at (Lower Bound. e.g., 450.00),
        - `priceAtMax`: filter by the price at (Upper Bound. e.g., 460.00),
        - `stopLossAtMin`: filter by the stop loss at (Lower Bound. e.g., 450.00),
        - `stopLossAtMax`: filter by the stop loss at (Upper Bound. e.g., 460.00),
        - `takeProfitAtMin`: filter by the take profit at (Lower Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `takeProfitAtMax`: filter by the take profit at (Upper Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `createdAtMin`: filter by the date of creating the trade (Lower Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `createdAtMax`: filter by the date of creating the trade (Upper Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `updatedAtMin`: filter by the date of updating the trade (Lower Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `updatedAtMax`: filter by the date of updating the trade (Upper Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `tradeAtMin`: filter by the trade at (Lower Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `tradeAtMax`: filter by the trade at (Upper Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `marketDataType`: filter by market data type (e.g., XAUUSD),
        - `tradeStatus`: filter by trade status (e.g., PENDING, RUNNING, PROFIT, LOSS, CANCELLED, EXPIRED),
        - `tradeType`:  filter by trade type,
        - `closedAtMin`: filter by the date of closing the trade (Lower Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `closedAtMax`: filter by the date of closing the trade (Upper Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `page`: page number (starting from 0),
        - `size`: number of items per page,
        - `sortBy`: field to sort by (e.g., createdAt, updatedAt, etc),
        - `direction`: sort direction (e.g., ASC, DESC)
    * **Data Response: List of (TradeResponse [(See below)](#traderesponse))**
* **`GET {Trade Base Path}/{id}` - Get a specific trade**
    * **Description:** Retrieves a specific trade by its ID.
    * **Roles Required:** Admin
    * **Request:** (No request body needed)
    * **Data Response: (TradeResponse [(See below)](#traderesponse))**
* **`GET {Trade Base Path}/mine/{id}` - Get your own specific trade by ID**
    * **Description:** Retrieves your own specific trade by its ID.
    * **Roles Required:** Customer
    * **Request:** (No request body needed)
    * **Data Response: (TradeResponse [(See below)](#traderesponse))**
* **`PUT {Trade Base Path}/mine/{id}` - Update a specific trade by ID**
    * **Description:** Updates a specific trade by its ID.
    * **Roles Required:** Customer
    * **Request: (TradeRequest [(See below)](#traderequest). PriceAt, TradeAt, Lot and TradeType cannot be updated)**
    * **Data Response: (TradeResponse [(See below)](#traderesponse))**
* **`PUT {Trade Base Path}/mine/{id}/cancel` - Cancel a specific trade by ID**
    * **Description:** Cancels your own specific trade by its ID.
    * **Roles Required:** Customer
    * **Request:** (No request body needed)
    * **Data Response: (TradeResponse [(See below)](#traderesponse))**

</details>

<details>
<summary><h2>Top Up Service</h2></summary>

### Top Up Service

**Top Up Base Path:** `/api/top-up`

* **`POST {Top Up Base Path}/me` - Create your own top up**
    * **Description:** Creates a new top up.
    * **Roles Required:** Customer
    * **Request: (TopUpRequest [(See below)](#topuprequest))**
    * **Data Response: (TopUpResponse [(See below)](#topupresponse))**
* **`GET {Top Up Base Path}/me` - Get your own top up**
    * **Description:** Retrieves your own top up.
    * **Roles Required:** Customer
    * **Request:** (No request body needed)
    * **Request Parameters:**
        - `amountMin`: filter by the top up amount (Lower Bound),
        - `amountMax`: filter by the top up amount (Upper Bound),
        - `paymentStatus`: filter by payment status (e.g., PENDING, SUCCESS, FAILED),
        - `paymentType`: filter by payment type (e.g., BANK TRANSFER, PAYPAL),
        - `expiredAtMin`: filter by the date of expiring the top up (Lower Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `expiredAtMax`: filter by the date of expiring the top up (Upper Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `createdAtMin`: filter by the date of creating the top up (Lower Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `createdAtMax`: filter by the date of creating the top up (Upper Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `updatedAtMin`: filter by the date of updating the top up (Lower Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `updatedAtMax`: filter by the date of updating the top up (Upper Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `page`: page number (starting from 0),
        - `size`: number of items per page,
        - `sortBy`: field to sort by (e.g., createdAt, updatedAt, etc),
        - `direction`: sort direction (e.g., ASC, DESC)
    * **Data Response: List of (TopUpResponse [(See below)](#topupresponse))**
* **`GET {Top Up Base Path}/me/{id}` - Get your own specific top up by ID**
    * **Description:** Retrieves your own specific top up by its ID.
    * **Roles Required:** Customer
    * **Request:** (No request body needed)
    * **Data Response: (TopUpResponse [(See below)](#topupresponse))**
* **`PUT {Top Up Base Path}/mine/{id}/pay` - Pay a specific top up by ID**
    * **Description:** Pays your own specific top up by its ID.
    * **Roles Required:** Customer
    * **Request:** (No request body needed)
    * **Data Response: (TopUpResponse [(See below)](#topupresponse))**
* **`GET {Top Up Base Path}` - Get all top ups**
    * **Description:** Retrieves a list of all top ups.
    * **Roles Required:** Admin
    * **Request:** (No request body needed)
    * **Request Parameters:**
        - `amountMin`: filter by the top up amount (Lower Bound),
        - `amountMax`: filter by the top up amount (Upper Bound),
        - `paymentStatus`: filter by payment status (e.g., PENDING, SUCCESS, FAILED),
        - `paymentType`: filter by payment type (e.g., BANK TRANSFER, PAYPAL),
        - `expiredAtMin`: filter by the date of expiring the top up (Lower Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `expiredAtMax`: filter by the date of expiring the top up (Upper Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `createdAtMin`: filter by the date of creating the top up (Lower Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `createdAtMax`: filter by the date of creating the top up (Upper Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `updatedAtMin`: filter by the date of updating the top up (Lower Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `updatedAtMax`: filter by the date of updating the top up (Upper Bound. e.g., YYYY-MM-DDTHH:mm:ssZ),
        - `page`: page number (starting from 0),
        - `size`: number of items per page,
        - `sortBy`: field to sort by (e.g., createdAt, updatedAt, etc),
        - `direction`: sort direction (e.g., ASC, DESC)
    * **Data Response: List of (TopUpResponse [(See below)](#topupresponse))**
* **`GET {Top Up Base Path}/{id}` - Get a specific top up by ID**
    * **Description:** Retrieves a specific top up by its ID.
    * **Roles Required:** Admin
    * **Request:** (No request body needed)
    * **Data Response: (TopUpResponse [(See below)](#topupresponse))**

</details>

---

## 7. Models

---

### Auth Service Model

#### Auth Service Request

##### LoginRequest
        ```json
        {
            "username": "john.doe@example.com",
            "password": "securepassword123"
        }
        ```

##### RegisterRequest
        ```json
        {
            "username": "johndoe123" [3, 20] characters,
            "email": "john.doe@example.com" [8, 40] characters,
            "password": "securepassword123"
        }
        ```

##### RefreshTokenRequest
        ```json
        {
            "refreshToken": "refresh-token-example"
        }
        ```

#### Auth Service Response

##### TokenResponse
        ```json
        {
            "accessToken": "access-token-example",
            "refreshToken": "refresh-token-example"
        }
        ```

---

### Customer Service Model

#### Customer Service Request

##### CustomerRequest
        ```json
        {
            "fullname": "John",
            "address": "123 Main Street",
            "birthDate": "1990-01-01"
        }
        ```

#### Customer Service Response

##### CustomerResponse
        ```json
        {
            "id": "id-example",
            "fullname": "john",
            "address": "123 Main Street",
            "birthDate": "1990-01-01",
            "balance": 5000.0,
            "createdAt": "2022-01-01T00:00:00Z",
            "updatedAt": "2022-01-01T00:00:00Z",
            "userId": "user-id-example",
            "isActive": true
        }
        ```

---

### Market Data Service Model

#### Market Data Service Request

##### QuizRequest
        ```json
        {
            "nSize": "tiny" ["tiny", "standard", "large"]
        }
        ```

##### AnswerRequest
        ```json
        {
            "priceAt": 540.00,
            "takeProfitAt": 550.00,
            "stopLossAt": 530.00
        }
        ```

#### Market Data Service Response

##### MarketDataResponse
        ```json
        {
            "timeBucketStartMin": "2022-01-02T00:00:00Z",
            "timeBucketEndMax": "2022-01-01T00:00:00Z",
            "marketDataType": "XAUUSD",
            "dataCount": 120,
            "marketData": [
                {
                    "timeBucketStart": "2022-01-02T00:00:00Z",
                    "low": 540.00,
                    "high": 550.00,
                    "closed": 545.00,
                    "open": 545.00,
                    "volume": 100
                },
                ...
            ]
        }
        ```

##### QuizResponse
        ```json
        {
            "id": "id-example",
            "userId": "user-id-example",
            "createdAt": "2022-01-01T00:00:00Z",
            "updatedAt": "2022-01-01T00:00:00Z",
            "answer": {
                AnswerResponse
            },
            "dataCount": 120,
            "quizMarketData": [
                {
                    "timeBucketStart": "2022-01-02T00:00:00Z",
                    "low": 540.00,
                    "high": 550.00,
                    "closed": 545.00,
                    "open": 545.00,
                    "volume": 100
                },
                ...
            ],
            "nsize": "TINY"
        }
        ```

##### AnswerResponse
        ```json
        {
            "result": -10.0,
            "priceAt": 540.00,
            "takeProfitAt": 550.00,
            "stopLossAt": 530.00,
            "dataCount": 60,
            "answerMarketData": [
                {
                    "timeBucketStart": "2022-01-30T00:00:00Z",
                    "low": 540.00,
                    "high": 550.00,
                    "closed": 545.00,
                    "open": 545.00,
                    "volume": 100
                },
                ...
            ]
        }
        ```

---

### Trade Service Model

#### Trade Service Request

##### TradeRequest
        ```json
        {
            "lot": 0.01 [0, 10],
            "priceAt": 350.0,
            "stopLossAt": 400.0,
            "takeProfitAt": 300.0,
            "tradeAt": "2022-01-01T00:00:00Z",
            "marketDataType": "XAUUSD",
            "tradeType": "SELL STOP" [BUY LIMIT, BUY STOP, SELL LIMIT, SELL STOP, MARKET EXECUTION BUY, MARKET EXECUTION SELL],
            "expiredAt": "2022-01-01T00:00:00Z" (optional),
        }
        ```

#### Trade Service Response

##### TradeResponse
        ```json
        {
            "id": "id-example",
            "userId": "user-id-example",
            "priceAt": 350.0,
            "stopLossAt": 400.0,
            "takeProfitAt": 300.0,
            "createdAt": "2022-01-01T00:00:00Z",
            "updatedAt": "2022-01-01T00:00:00Z",
            "tradeAt": "2022-01-01T00:00:00Z",
            "lot": 0.01,
            "marketDataType": "XAUUSD",
            "tradeStatus": "RUNNING" [RUNNING, PENDING, EXPIRED, CANCELLED, LOSS, PROFIT],
            "tradeType": "SELL STOP",
            "closedAt": "2022-01-02T00:00:00Z",
            "expiredAt": "2022-01-02T00:00:00Z",
        }
        ```

---

### Top Up Service Model

#### Top Up Service Request

##### TopUpRequest
        ```json
        {
            "amount": 100.0,
            "paymentType": "BANK TRANSFER" [BANK TRANSFER, PAYPAL, DANA],
        }
        ```

#### Top Up Service Response

##### TopUpResponse
        ```json
        {
            "id": "id-example",
            "userId": "user-id-example",
            "amount": 100.0,
            "paymentType": "BANK TRANSFER",
            "paymentStatus": "PENDING" [PENDING, SUCCESS, FAILED],
            "createdAt": "2022-01-01T00:00:00Z",
            "updatedAt": "2022-01-01T00:00:00Z",
            "expiredAt": "2022-01-02T00:00:00Z"
        }

---