package com.example.GamesHubMobileBackend;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GamesHubMobileBackendApplication {

	@Value("${stripe.api.key}")
	private String stripeApiKey;

	public static void main(String[] args) {
		SpringApplication.run(GamesHubMobileBackendApplication.class, args);
	}

	@PostConstruct
	public void init() {
		Stripe.apiKey = stripeApiKey;
	}
}
