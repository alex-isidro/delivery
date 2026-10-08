package br.com.fiap.delivery.order.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayDeque;
import java.util.Deque;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final int MAX_REQUESTS = 20;
    private static final long WINDOW_MILLIS = 1_000;

    private final Deque<Long> requests = new ArrayDeque<>();

    @Override
    public synchronized boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        long now = System.currentTimeMillis();

        while (!requests.isEmpty()
                && now - requests.peekFirst() >= WINDOW_MILLIS) {
            requests.removeFirst();
        }

        if (requests.size() >= MAX_REQUESTS) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            try {
                response.getWriter().write(
                        "{\"error\":\"Rate limit exceeded\"}"
                );
            } catch (Exception ignored) {
            }
            return false;
        }

        requests.addLast(now);
        return true;
    }
}