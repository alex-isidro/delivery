package br.com.fiap.delivery.order.service;

import br.com.fiap.delivery.order.entity.Dish;
import br.com.fiap.delivery.order.repository.DishRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final DishRepository dishRepository;

    private static final String CONVERSATION_ID = "delivery-chat";

    private final String systemMessage = """
            Você é o atendente de um restaurante.
            Responda de forma curta e objetiva, em português.
            Responda somente sobre o restaurante, seu cardápio, ingredientes, preços,
            disponibilidade e sugestões de pratos.
            Perguntas fora desse tema devem ser recusadas educadamente e você deve voltar ao assunto do restaurante.
            """;

    public ChatService(
            ChatClient.Builder builder,
            DishRepository dishRepository,
            ChatMemory chatMemory
    ) {
        this.dishRepository = dishRepository;

        this.chatClient = builder
                .defaultSystem(systemMessage)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }

    public String answer(String question) {

        List<Dish> dishes = dishRepository.findAll();

        String menu = dishes.stream()
                .map(d -> d.getName()
                        + " | R$ " + d.getPrice()
                        + " | stock: " + d.getStock())
                .reduce("", (a, b) -> a + b + "\n");

        return chatClient.prompt()
                .user("""
                        Cardápio atual:
                        %s
                        Pergunta: %s
                        """.formatted(menu, question))
                .advisors(a -> a.param(
                        ChatMemory.CONVERSATION_ID,
                        CONVERSATION_ID
                ))
                .call()
                .content();
    }
}