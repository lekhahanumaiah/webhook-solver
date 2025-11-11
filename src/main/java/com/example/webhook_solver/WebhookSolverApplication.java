package com.example.webhook_solver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@SpringBootApplication
public class WebhookSolverApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebhookSolverApplication.class, args);
	}

	@Bean
	CommandLineRunner run(RestTemplate restTemplate) {
		return args -> {

			// Step 1: Generate webhook
			String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
			Map<String, String> requestBody = Map.of(
					"name", "John Doe",
					"regNo", "REG12347",
					"email", "john@example.com"
			);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

			ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

			System.out.println("Response from generateWebhook: " + response.getBody());

			String webhookUrl = (String) response.getBody().get("webhook");
			String accessToken = (String) response.getBody().get("accessToken");

			// Step 2: Prepare your final SQL query
			String finalQuery = "SELECT * FROM employees WHERE salary = (SELECT MAX(salary) FROM employees);";

			// Step 3: Submit final query
			HttpHeaders headers2 = new HttpHeaders();
			headers2.setContentType(MediaType.APPLICATION_JSON);
			headers2.set("Authorization", accessToken);

			Map<String, String> finalBody = Map.of("finalQuery", finalQuery);

			HttpEntity<Map<String, String>> entity2 = new HttpEntity<>(finalBody, headers2);

			ResponseEntity<String> submitResponse = restTemplate.postForEntity(webhookUrl, entity2, String.class);

			System.out.println("Submission Response: " + submitResponse.getBody());
		};
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
}

