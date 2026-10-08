package br.com.fiap.delivery.order.service;

import br.com.fiap.delivery.order.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReviewPublisher {
    private final RabbitTemplate rabbitTemplate;

    public ReviewPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(Long dishId, String dishName, int rating, String comment) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY,
                Map.of("dishId", dishId, "dishName", dishName, "rating", rating, "comment", comment)
        );
    }
}
