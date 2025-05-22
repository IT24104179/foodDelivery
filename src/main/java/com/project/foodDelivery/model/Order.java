package com.project.foodDelivery.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Order implements Serializable {
    private String id;
    private String foodItemId;
    private String foodName;
    private int quantity;
    private double price; // Price per item
    private double totalAmount; // Total order amount (price * quantity)
    private String address;
    private String notes;
    private String customerId;
    private String customerName;
    private String orderDate; // ISO string
    private String status;

    public Order() {}

    public Order(String id, String foodItemId, String foodName, int quantity, double price, double totalAmount, String address, String notes, String customerId, String customerName, String orderDate, String status) {
        this.id = id;
        this.foodItemId = foodItemId;
        this.foodName = foodName;
        this.quantity = quantity;
        this.price = price;
        this.totalAmount = totalAmount;
        this.address = address;
        this.notes = notes;
        this.customerId = customerId;
        this.customerName = customerName;
        this.orderDate = orderDate;
        this.status = status;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFoodItemId() { return foodItemId; }
    public void setFoodItemId(String foodItemId) { this.foodItemId = foodItemId; }
    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String toFileString() {
        // Use | as separator to avoid comma issues in address/notes
        return id + "|" + foodItemId + "|" + foodName + "|" + quantity + "|" + price + "|" + totalAmount + "|" + 
               address.replace("|", "/") + "|" + notes.replace("|", "/") + "|" + 
               customerId + "|" + customerName + "|" + orderDate + "|" + status;
    }

    public static Order fromFileString(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 12) return null;
        return new Order(
            p[0], p[1], p[2], Integer.parseInt(p[3]), 
            Double.parseDouble(p[4]), Double.parseDouble(p[5]), 
            p[6], p[7], p[8], p[9], p[10], p[11]
        );
    }
} 