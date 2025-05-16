package com.fooddelivery.deliverymanagement.service;

import com.fooddelivery.deliverymanagement.model.Delivery;
import com.fooddelivery.deliverymanagement.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;

    @Autowired
    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    // Assign a new delivery
    public Delivery assignDelivery(String orderId, String deliveryPersonId) {
        return deliveryRepository.createDelivery(orderId, deliveryPersonId);
    }

    // Get all active deliveries
    public List<Delivery> getAllActiveDeliveries() {
        return deliveryRepository.getAllActiveDeliveries();
    }

    // Get delivery history
    public List<Delivery> getDeliveryHistory() {
        return deliveryRepository.getDeliveryHistory();
    }

    // Update delivery status
    public void updateDeliveryStatus(String deliveryId, String newStatus) {
        deliveryRepository.updateDeliveryStatus(deliveryId, newStatus);
    }

    // Remove completed delivery
    public void removeCompletedDelivery(String deliveryId) {
        deliveryRepository.deleteCompletedDelivery(deliveryId);
    }
}
