package br.com.fiap.delivery.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(
        @NotNull Long dishId,
        @Min(1) int quantity
) {}
