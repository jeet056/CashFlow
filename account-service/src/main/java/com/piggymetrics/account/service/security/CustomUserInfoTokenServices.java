package com.piggymetrics.account.service.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

public class CustomUserInfoTokenServices implements OpaqueTokenIntrospector {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserInfoTokenServices.class);
    private static final String[] PRINCIPAL_KEYS = new String[] { "user", "username",
            "userid", "user_id", "login", "id", "name" };

    private final String userInfoEndpointUrl;
    private final String clientId;
    private final WebClient webClient;

    public CustomUserInfoTokenServices(String userInfoEndpointUrl, String clientId, WebClient webClient) {
        this.userInfoEndpointUrl = userInfoEndpointUrl;
        this.clientId = clientId;
        this.webClient = webClient;
    }

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        Map<String, Object> userInfo = getUserInfo(token);
        if (userInfo.containsKey("error")) {
            logger.debug("userinfo returned error: {}", userInfo.get("error"));
            throw new OAuth2IntrospectionException("Invalid token: " + token);
        }
        Object principal = extractPrincipal(userInfo);
        Set<String> scopes = extractScopes(userInfo);
        // Optionally extract granted authorities if needed.
        Collection authorities = Collections.emptyList();
        // Embed additional client information if needed.
        Map<String, Object> attributes = new HashMap<>(userInfo);
        attributes.put("client_id", this.clientId);
        return new DefaultOAuth2AuthenticatedPrincipal(principal.toString(), attributes, authorities);
    }

    private Object extractPrincipal(Map<String, Object> userInfo) {
        for (String key : PRINCIPAL_KEYS) {
            if (userInfo.containsKey(key)) {
                return userInfo.get(key);
            }
        }
        return "unknown";
    }

    @SuppressWarnings("unchecked")
    private Set<String> extractScopes(Map<String, Object> userInfo) {
        Set<String> scopes = new LinkedHashSet<>();
        Object scopeObj = userInfo.get("scope");
        if (scopeObj instanceof String) {
            scopes.addAll(Arrays.asList(StringUtils.delimitedListToStringArray((String) scopeObj, " ")));
        } else if (scopeObj instanceof Collection) {
            scopes.addAll((Collection<String>) scopeObj);
        }
        return scopes;
    }

    private Map<String, Object> getUserInfo(String token) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                        .path(userInfoEndpointUrl)
                        .queryParam("access_token", token)
                        .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception ex) {
            logger.info("Could not fetch user details: {}: {}", ex.getClass(), ex.getMessage());
            return Collections.singletonMap("error", "Could not fetch user details");
        }
    }
}
