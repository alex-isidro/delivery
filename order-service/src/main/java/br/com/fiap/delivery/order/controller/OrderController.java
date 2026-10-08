package br.com.fiap.delivery.order.controller;

import br.com.fiap.delivery.order.dto.AssistantRequest;
import br.com.fiap.delivery.order.entity.CustomerOrder;
import br.com.fiap.delivery.order.entity.Dish;
import br.com.fiap.delivery.order.dto.OrderRequest;
import br.com.fiap.delivery.order.dto.ReviewRequest;
import br.com.fiap.delivery.order.repository.DishRepository;
import br.com.fiap.delivery.order.service.ChatService;
import br.com.fiap.delivery.order.service.OrderService;
import br.com.fiap.delivery.order.service.ReviewPublisher;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class OrderController {
    private final DishRepository dishRepository;
    private final OrderService orderService;
    private final ReviewPublisher reviewPublisher;
    private final ChatService chatService;

    public OrderController(DishRepository dishRepository, OrderService orderService, ReviewPublisher reviewPublisher, ChatService chatService) {
        this.dishRepository = dishRepository;
        this.orderService = orderService;
        this.reviewPublisher = reviewPublisher;
        this.chatService = chatService;
    }

    @GetMapping("/dishes")
    public List<Dish> dishes() {
        return dishRepository.findAll();
    }

    @GetMapping("/dishes/{id}")
    public Dish dish(@PathVariable Long id) {
        return dishRepository.findById(id)
                .orElseThrow(() -> new OrderService.DishNotFoundException("Dish not found"));
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerOrder createOrder(@Valid @RequestBody OrderRequest request) {
        return orderService.create(request);
    }

    @GetMapping("/orders/{id}")
    public CustomerOrder order(@PathVariable Long id) {
        return orderService.find(id);
    }

    @PostMapping("/reviews")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void review(@Valid @RequestBody ReviewRequest request) {
        Dish dish = dish(request.dishId());
        reviewPublisher.publish(dish.getId(), dish.getName(), request.rating(), request.comment());
    }

    @PostMapping("/assistant")
    public Map<String, String> assistant(@Valid @RequestBody AssistantRequest request) {
        return Map.of("answer", chatService.answer(request.question()));
    }
}
