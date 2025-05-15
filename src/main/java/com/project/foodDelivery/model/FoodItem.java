package com.foodDelivery.project.model;

import java.io.Serializable;

public class FoodItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name;
    private double price;
    private String description;
    private String imageUrl;
    private boolean available;
    private String type; // Can be "veg" or "non-veg"
    
    public FoodItem() {
    }
    
    public FoodItem(String id, String name, double price, String description, String imageUrl, boolean available, String type) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.available = available;
        this.type = type;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        // Handle null values to prevent empty fields
        String desc = (description == null || description.isEmpty()) ? "No description" : description;
        String img = (imageUrl == null) ? "" : imageUrl;
        
        return id + "," + name + "," + price + "," + desc + "," + img + "," + available + "," + type;
    }
}
