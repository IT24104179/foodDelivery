package com.project.foodDelivery.service;

import com.project.foodDelivery.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    @Autowired
    private FoodItemService foodItemService;
    
    public Order placeOrder(String foodItemId, String foodName, int quantity, String address, String notes, String customerId, String customerName) {
        String id = UUID.randomUUID().toString();
        String orderDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String status = "Placed";
        
        // Get the food item price
        double price = 0.0;
        try {
            // Try to get the price from the food item service
            com.project.foodDelivery.model.FoodItem foodItem = foodItemService.getFoodItemById(foodItemId);
            if (foodItem != null) {
                price = foodItem.getPrice();
            }
        } catch (Exception e) {
            System.err.println("Error getting food item price: " + e.getMessage());
            // If we can't get the price, we'll use 0.0
        }
        
        // Calculate the total amount
        double totalAmount = price * quantity;
        
        Order order = new Order(id, foodItemId, foodName, quantity, price, totalAmount, address, notes, customerId, customerName, orderDate, status);
        OrderFileHandler.saveOrder(order);
        return order;
    }

    public List<Order> getOrdersByCustomerId(String customerId) {
        return OrderFileHandler.findOrdersByCustomerId(customerId);
    }

    public List<Order> getAllOrders() {
        return OrderFileHandler.readAllOrders();
    }

    public Order getOrderById(String id) {
        return OrderFileHandler.findOrderById(id);
    }

    public void updateOrderStatus(String orderId, String status) {
        Order order = getOrderById(orderId);
        if (order != null) {
            order.setStatus(status);
            OrderFileHandler.updateOrder(order);
        }
    }
} 