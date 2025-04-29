# CashFlow

A cloud-native, microservice-based financial management platform that enables users to track income, expenses, savings, and gain actionable financial insights.

# Features

Secure OAuth2-based user authentication and authorization.

Track income, expenses, and savings with personalized account settings.

Time-series financial data aggregation and analysis for insights.

Scheduled notifications and financial reminders.

Fully containerized using Docker for easy deployment.

# Microservices Overview

Auth Service: Handles user registration, login, and OAuth2-based security.

Account Service: Manages financial transactions including income, expenses, and savings.

Statistics Service: Processes and stores normalized time-series data for financial analytics.

Notification Service: Sends scheduled notifications based on user-defined preferences.

# Infrastructure Components

API Gateway: Routes and filters external requests to appropriate services using Spring Cloud Gateway.

Config Server: Centralized configuration management with dynamic updates.

Service Discovery: Implemented using Consul for service registration and discovery.

Circuit Breaker and Resilience: Handled with Resilience4j to ensure fault tolerance.

Distributed Tracing and Logging: Integrated with Micrometer and OpenTelemetry for observability.

# Tech Stack

Languages: Java

Frameworks: Spring Boot, Spring Cloud

Databases: MongoDB

Message Broker: RabbitMQ

Containerization: Docker, Docker Compose

Monitoring and Tracing: Micrometer, OpenTelemetry

Other: OAuth2, Consul, Resilience4j, Spring Cloud Gateway
