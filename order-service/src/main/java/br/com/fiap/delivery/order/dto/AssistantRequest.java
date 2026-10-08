package br.com.fiap.delivery.order.dto;

import jakarta.validation.constraints.NotBlank;

public record AssistantRequest(@NotBlank String question) {}
