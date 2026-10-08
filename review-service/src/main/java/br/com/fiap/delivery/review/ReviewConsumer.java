package br.com.fiap.delivery.review;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ReviewConsumer {
    private static final Logger log = LoggerFactory.getLogger(ReviewConsumer.class);

    private record Accumulator(String dishName, int totalRating, int count) {
        Accumulator add(int rating) {
            return new Accumulator(dishName, totalRating + rating, count + 1);
        }
    }

    private final ConcurrentHashMap<Long, Accumulator> reviews = new ConcurrentHashMap<>();
    private final ReviewSummaryRepository repository;

    public ReviewConsumer(ReviewSummaryRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void consume(Map<String, Object> message) {
        Long dishId = ((Number) message.get("dishId")).longValue();
        String dishName = String.valueOf(message.get("dishName"));
        int rating = ((Number) message.get("rating")).intValue();
        reviews.compute(dishId, (id, old) -> old == null
                ? new Accumulator(dishName, rating, 1)
                : old.add(rating));
        log.info("Review received for dish {}", dishId);
    }

    @Scheduled(fixedDelay = 5_000)
    public void flush() {
        log.info("Flushing review buffer");
        reviews.forEach((dishId, accumulator) -> {
            ReviewSummary summary = repository.findByDishId(dishId)
                    .orElseGet(() -> new ReviewSummary(dishId, accumulator.dishName(), 0, 0));
            summary.setDishName(accumulator.dishName());
            summary.setTotalRating(summary.getTotalRating() + accumulator.totalRating());
            summary.setCount(summary.getCount() + accumulator.count());
            repository.save(summary);
            reviews.remove(dishId, accumulator);
        });
    }
}
