package com.fooddelivery.orderbackend.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Order {
    private Long orderId;
    private String userId;
    private double totalPrice;
    private LocalDateTime time;
    private List<FoodItem> foodItems;
}
