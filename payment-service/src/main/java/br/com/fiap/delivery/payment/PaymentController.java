package br.com.fiap.delivery.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;

@RestController
public class PaymentController {
    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final int port;
    private final Random random = new Random();

    public PaymentController(@Value("${server.port}") int port) {
        this.port = port;
    }

    @PostMapping("/payments")
    public Map<String, Object> pay(@RequestBody PaymentRequest request) {
        if (request.amount() == null || request.amount().signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be greater than zero");
        }

        if (random.nextBoolean()) {

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Payment failed");
        }

        return Map.of("status", "APPROVED", "instance", port);
    }

    public record PaymentRequest(BigDecimal amount) {}
}
