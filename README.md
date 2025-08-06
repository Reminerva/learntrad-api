# LearnTrad API Documentation

To use this app, switch to branch "development" or "deployment".

## LearnTrad API

LearnTrad API is a microservices-based backend system built to support a trading education platform. It provides services such as authentication, customer and admin management, market data handling, and trade placing and processing — all designed using Spring Boot and deployed using Kubernetes.

A key feature of this platform is the Quiz Fetching system, where customers receive anonymous, preprocessed time-series data in the form of candlestick charts. The data is intentionally obfuscated — its real asset name, time frame, and price scale are hidden or altered — to ensure users cannot look up actual historical prices while solving the quiz. This encourages independent analysis and strengthens data interpretation skills.

This system is developed for educational purposes, and serves as a scalable simulation of real-world trading services and challenges.

**App Architecture** :

**ERD** : 
