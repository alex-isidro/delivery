package br.com.fiap.delivery.review;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ReviewSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long dishId;
    private String dishName;
    private int totalRating;
    private int count;

    public ReviewSummary() {}

    public ReviewSummary(Long dishId, String dishName, int totalRating, int count) {
        this.dishId = dishId;
        this.dishName = dishName;
        this.totalRating = totalRating;
        this.count = count;
    }

    public Long getId() { return id; }
    public Long getDishId() { return dishId; }
    public String getDishName() { return dishName; }
    public int getTotalRating() { return totalRating; }
    public int getCount() { return count; }
    public void setId(Long id) { this.id = id; }
    public void setDishId(Long dishId) { this.dishId = dishId; }
    public void setDishName(String dishName) { this.dishName = dishName; }
    public void setTotalRating(int totalRating) { this.totalRating = totalRating; }
    public void setCount(int count) { this.count = count; }
}
