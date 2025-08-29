package services.cashflow.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r
                    .path("/uaa/**")
                    .filters(f -> f.rewritePath("/uaa/(?<segment>.*)", "/${segment}"))
                    .uri("lb://auth-service"))
                .route("account-service", r -> r
                    .path("/accounts/**")
                    .uri("lb://account-service"))
                .route("statistics-service", r -> r
                    .path("/statistics/**")
                    .uri("lb://statistics-service"))
                .route("notification-service", r -> r
                    .path("/notifications/**")
                    .uri("lb://notification-service"))
                .build();
    }
}
