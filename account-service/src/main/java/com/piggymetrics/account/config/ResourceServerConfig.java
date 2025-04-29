package com.piggymetrics.account.config;

import com.piggymetrics.account.service.security.CustomUserInfoTokenServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ResourceServerConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, OpaqueTokenIntrospector introspector) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/demo").permitAll()
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2
                .opaqueToken(opaque -> opaque
                    .introspector(introspector)));
        return http.build();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    @Bean
    public OpaqueTokenIntrospector introspector(WebClient webClient) {
        // Replace the URLs and clientId with your own configuration
        String userInfoEndpointUrl = "http://auth-service:5000/uaa/userinfo";
        String clientId = "your-client-id";
        return new CustomUserInfoTokenServices(userInfoEndpointUrl, clientId, webClient);
    }
}
