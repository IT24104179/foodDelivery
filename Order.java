package com.fooddelivery.orderbackend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "`orders`")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    private String userId;
    private double totalPrice;
    private LocalDateTime time;

    @ElementCollection
    @CollectionTable(
            name = "order_food_items",
            joinColumns = @JoinColumn(name = "order_id")
    )
    private List<FoodItem> foodItems;
}
