package br.com.fiap.delivery.order.service;

import br.com.fiap.delivery.order.entity.CustomerOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class PaymentClient {
    private static final Logger log = LoggerFactory.getLogger(PaymentClient.class);
    private final RestTemplate restTemplate;

    public PaymentClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Retryable(maxRetries = 4, delay = 100, multiplier = 2, jitter = 100, maxDelay = 1500)
    public void pay(CustomerOrder order) {
        BigDecimal amount = order.getTotalPrice();
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "http://PAYMENT-SERVICE/payments",
                Map.of("amount", amount),
                Map.class
        );
        log.info("Payment response: {}", response.getBody());
    }
}
