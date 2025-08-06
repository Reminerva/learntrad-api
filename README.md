# LearnTrad

**To explore and use this application, you'll need to switch to either the **"development"** or **"deployment"** branch. Each branch contains instructions within its `README` on how to set up and run the application. Follow those guidelines to get started!**

## LearnTrad API

LearnTrad API is a microservices-based backend system built to support a trading education platform. It provides services such as authentication, customer and admin management, market data handling, and trade placing and processing — all designed using Spring Boot and deployed using Kubernetes.

A key feature of this platform is the Quiz Fetching system, where customers receive anonymous, preprocessed time-series data in the form of candlestick charts. The data is intentionally obfuscated — its real asset name, time frame, and price scale are hidden or altered — to ensure users cannot look up actual historical prices while solving the quiz. This encourages independent analysis and strengthens data interpretation skills.

This system is developed for educational purposes, and serves as a scalable simulation of real-world trading services and challenges.

**App Architecture** : [**Lucid.app**](https://lucid.app/lucidspark/a21cf1a0-0350-4597-a55b-e923e33a341f/edit?viewport_loc=-814%2C-322%2C3709%2C2024%2C0_0&invitationId=inv_9565557f-f02d-4240-842e-333418859bd4)

**ERD** : [**Eraser.io**](https://app.eraser.io/workspace/XkP12lB6MXvPzm879YFq?origin=share)

## LearnTrad Features

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
