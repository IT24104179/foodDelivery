package com.fooddelivery.deliverymanagement.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Delivery implements Serializable {
    private String deliveryId;
    private String orderId;
    private String deliveryPersonId;
    private String status;
    private LocalDateTime assignedTime;
    private LocalDateTime deliveryTime;

    // Constructors
    public Delivery() {}

    public Delivery(String deliveryId, String orderId, String deliveryPersonId) {
        this.deliveryId = deliveryId;
        this.orderId = orderId;
        this.deliveryPersonId = deliveryPersonId;
        this.status = "Assigned";
        this.assignedTime = LocalDateTime.now();
    }

    // Getters and Setters
    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDeliveryPersonId() {
        return deliveryPersonId;
    }

    public void setDeliveryPersonId(String deliveryPersonId) {
        this.deliveryPersonId = deliveryPersonId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getAssignedTime() {
        return assignedTime;
    }

    public void setAssignedTime(LocalDateTime assignedTime) {
        this.assignedTime = assignedTime;
    }

    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    // Polymorphic method for delivery time calculation
    public long calculateDeliveryDuration() {
        if (deliveryTime != null && assignedTime != null) {
            return java.time.Duration.between(assignedTime, deliveryTime).toMinutes();
        }
        return 0;
    }

    @Override
    public String toString() {
        return deliveryId + "," + orderId + "," + deliveryPersonId + "," + status + "," + 
               (assignedTime != null ? assignedTime.toString() : "N/A") + "," + 
               (deliveryTime != null ? deliveryTime.toString() : "N/A");
    }
}
