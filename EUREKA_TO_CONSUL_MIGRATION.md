# Eureka to Consul Migration

This document outlines the migration from Netflix Eureka to HashiCorp Consul for service discovery in the CashFlow microservices architecture.

## Changes Made

### 1. Registry Service Updates
- **File**: `registry/pom.xml`
  - Replaced `spring-cloud-starter-netflix-eureka-server` with `spring-cloud-starter-consul-server`

- **File**: `registry/src/main/java/services/cashflow/registry/RegistryApplication.java`
  - Replaced `@EnableEurekaServer` with `@EnableDiscoveryClient`
  - Updated import from `org.springframework.cloud.netflix.eureka.server.EnableEurekaServer` to `org.springframework.cloud.client.discovery.EnableDiscoveryClient`

- **File**: `registry/src/main/resources/bootstrap.yml`
  - Removed Eureka configuration
  - Added Consul configuration with host, port, and discovery settings

### 2. Client Services Updates
- **Account Service**: `account-service/pom.xml`
  - Replaced `spring-cloud-starter-netflix-eureka-client` with `spring-cloud-starter-consul-discovery`

- **Gateway Service**: `gateway/pom.xml`
  - Replaced `spring-cloud-starter-netflix-eureka-client` with `spring-cloud-starter-consul-discovery`

- **Other Services**: Already had Consul discovery dependencies configured

### 3. Configuration Updates
- **File**: `config/src/main/resources/shared/application.yml`
  - Replaced Eureka configuration with Consul configuration
  - Added health check settings for Consul

### 4. Docker Compose Updates
- **File**: `docker-compose.yml`
  - Replaced `registry` service with `consul` service
  - Updated service dependencies to include Consul
  - Added Consul configuration with UI enabled

- **File**: `docker-compose.dev.yml`
  - Replaced `registry` service with `consul` service
  - Added Consul configuration for development environment

### 5. Test Configuration Updates
Updated all test bootstrap files to use Consul configuration instead of Eureka:
- `account-service/src/test/resources/bootstrap.yml`
- `auth-service/src/test/resources/bootstrap.yml`
- `gateway/src/test/resources/bootstrap.yml`
- `notification-service/src/test/resources/bootstrap.yml`
- `statistics-service/src/test/resources/bootstrap.yml`
- `turbine-stream-service/src/test/resources/bootstrap.yml`
- `monitoring/src/test/resources/bootstrap.yml`

## Consul Configuration

### Key Settings
- **Host**: `consul` (Docker service name)
- **Port**: `8500` (Default Consul HTTP API port)
- **Discovery**: Enabled with IP address preference
- **Health Checks**: Configured with `/actuator/health` endpoint
- **UI**: Enabled for service visualization

### Health Check Configuration
```yaml
spring:
  cloud:
    consul:
      discovery:
        health-check-path: /actuator/health
        health-check-interval: 15s
```

## Benefits of Consul

1. **Service Discovery**: Automatic service registration and discovery
2. **Health Checking**: Built-in health monitoring with configurable checks
3. **Key-Value Store**: Additional configuration management capabilities
4. **Web UI**: Built-in dashboard for service visualization
5. **Multi-Datacenter Support**: Enterprise-ready for distributed deployments
6. **ACL Support**: Advanced security and access control

## Accessing Consul UI

After starting the services, you can access the Consul web UI at:
- **Production**: http://localhost:8500
- **Development**: http://localhost:8500

## Migration Verification

To verify the migration was successful:

1. Start the services using Docker Compose
2. Access Consul UI at http://localhost:8500
3. Verify all services are registered and healthy
4. Check service logs for any Consul-related errors
5. Test service-to-service communication

## Notes

- The registry service can now be removed entirely as Consul handles service discovery
- All services now use `@EnableDiscoveryClient` annotation
- Health checks are automatically configured for all services
- The migration maintains backward compatibility with existing service communication patterns
