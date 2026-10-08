package br.com.fiap.delivery.order.controller;

import br.com.fiap.delivery.order.service.OrderService;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(OrderService.DishNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> dishNotFound(Exception exception) {
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(OrderService.OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> orderNotFound(Exception exception) {
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(OrderService.OutOfStockException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> outOfStock(Exception exception) {
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> badRequest(Exception exception) {
        return Map.of("error", "Invalid request");
    }

}
