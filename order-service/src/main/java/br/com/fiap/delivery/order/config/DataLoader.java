package br.com.fiap.delivery.order.config;

import br.com.fiap.delivery.order.entity.Dish;
import br.com.fiap.delivery.order.repository.DishRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataLoader {
    @Bean
    CommandLineRunner loadDishes(DishRepository repository) {
        return args -> {
            if (repository.count() > 0) return;
            repository.save(new Dish("House Burger", "Brioche bun, beef and cheese", new BigDecimal("39.90"), 10));
            repository.save(new Dish("Veggie Burger", "Vegetable patty and fresh salad", new BigDecimal("35.90"), 20));
            repository.save(new Dish("Chicken Bowl", "Chicken, rice and vegetables", new BigDecimal("32.50"), 15));
            repository.save(new Dish("Margherita Pizza", "Tomato, mozzarella and basil", new BigDecimal("42.00"), 12));
            repository.save(new Dish("Pasta Alfredo", "Pasta with creamy sauce", new BigDecimal("31.90"), 18));
        };
    }
}
