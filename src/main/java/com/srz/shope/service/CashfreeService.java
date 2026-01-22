package com.srz.shope.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class CashfreeService {

    // This is a minimal skeleton. For full integration, set CASHFREE_APP_ID and CASHFREE_SECRET
    // as environment variables and implement the API calls per Cashfree docs.

    public String createPaymentRedirect(Long orderId, Double amount, String returnUrl) {
        String appId = System.getenv("CASHFREE_APP_ID");
        String secret = System.getenv("CASHFREE_SECRET");

        if (appId == null || secret == null) {
            // no credentials — return a mock success URL for local testing
            return "/payment/mock-success?orderId=" + orderId;
        }

        try {
            // Example: call Cashfree Orders API (this is a placeholder; adapt per latest docs)
            HttpClient client = HttpClient.newHttpClient();
            Map<String, Object> body = new HashMap<>();
            body.put("order_id", String.valueOf(orderId));
            body.put("order_amount", amount);
            body.put("order_currency", "INR");
            body.put("return_url", returnUrl);

            String json = "{ }"; // not implementing full JSON serializer here

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://test.cashfree.com/api/v2/cftoken/order"))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .header("x-client-id", appId)
                    .header("x-client-secret", secret)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                // Parse response and extract payment link/token — simplified here
                // For now, fallback to mock success
                return "/payment/mock-success?orderId=" + orderId;
            }
        } catch (Exception e) {
            // ignore and return mock URL
        }

        return "/payment/mock-success?orderId=" + orderId;
    }
}
