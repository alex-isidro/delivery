package br.com.fiap.delivery.review;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
public class ReviewController {
    private final ReviewSummaryRepository repository;

    public ReviewController(ReviewSummaryRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/reviews/ranking")
    public List<RankingItem> ranking() {
        return repository.findAll().stream()
                .map(item -> new RankingItem(item.getDishId(), item.getDishName(),
                        item.getCount() == 0 ? 0.0 : (double) item.getTotalRating() / item.getCount(),
                        item.getCount()))
                .sorted(Comparator.comparingDouble(RankingItem::average).reversed())
                .toList();
    }

    public record RankingItem(Long dishId, String dishName, double average, int count) {}
}
