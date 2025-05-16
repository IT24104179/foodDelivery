package com.fooddelivery.orderbackend.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class FoodItem {
    private String name;
    private double price;
    private int quantity;
}
