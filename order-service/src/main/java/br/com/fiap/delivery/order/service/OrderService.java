package br.com.fiap.delivery.order.service;

import br.com.fiap.delivery.order.entity.CustomerOrder;
import br.com.fiap.delivery.order.entity.Dish;
import br.com.fiap.delivery.order.dto.OrderRequest;
import br.com.fiap.delivery.order.repository.CustomerOrderRepository;
import br.com.fiap.delivery.order.repository.DishRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {
    private final DishRepository dishRepository;
    private final CustomerOrderRepository orderRepository;
    private final PaymentClient paymentClient;

    public OrderService(DishRepository dishRepository, CustomerOrderRepository orderRepository, PaymentClient paymentClient) {
        this.dishRepository = dishRepository;
        this.orderRepository = orderRepository;
        this.paymentClient = paymentClient;
    }

    @Transactional
    public CustomerOrder create(OrderRequest request) {
        Dish dish = dishRepository.findByIdForUpdate(request.dishId())
                .orElseThrow(() -> new DishNotFoundException("Dish not found"));

        if (dish.getStock() < request.quantity()) {
            throw new OutOfStockException("Dish out of stock");
        }

        dish.setStock(dish.getStock() - request.quantity());
        dishRepository.save(dish);

        CustomerOrder order = new CustomerOrder();
        order.setDishId(dish.getId());
        order.setQuantity(request.quantity());
        order.setTotalPrice(dish.getPrice().multiply(java.math.BigDecimal.valueOf(request.quantity())));
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        orderRepository.save(order);

        paymentClient.pay(order);

        order.setStatus("CONFIRMED");
        return orderRepository.save(order);
    }

    public CustomerOrder find(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }

    public static class DishNotFoundException extends RuntimeException {
        public DishNotFoundException(String message) { super(message); }
    }

    public static class OutOfStockException extends RuntimeException {
        public OutOfStockException(String message) { super(message); }
    }

    public static class OrderNotFoundException extends RuntimeException {
        public OrderNotFoundException(String message) { super(message); }
    }
}
