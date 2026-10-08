package br.com.fiap.delivery.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE_NAME = "delivery.exchange";
    public static final String QUEUE_NAME = "reviews.queue";
    public static final String ROUTING_KEY = "reviews.new";

    @Bean
    Queue reviewsQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    TopicExchange deliveryExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    Binding reviewsBinding() {
        return BindingBuilder.bind(reviewsQueue()).to(deliveryExchange()).with(ROUTING_KEY);
    }
}
