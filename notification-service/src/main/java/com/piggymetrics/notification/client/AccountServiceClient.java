package com.piggymetrics.notification.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "account-service")
public interface AccountServiceClient {

	@CircuitBreaker(name = "accountServiceClient", fallbackMethod = "getAccountFallback")
	@GetMapping(value = "/accounts/{accountName}", consumes = MediaType.APPLICATION_JSON_VALUE)
	String getAccount(@PathVariable("accountName") String accountName);

	default String getAccountFallback(String accountName, Exception e) {
		return "Fallback response for account: " + accountName;
	}
}
