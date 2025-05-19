package com.project.foodDelivery.service;

import com.project.foodDelivery.model.Delivery;
import com.project.foodDelivery.model.Order;
import com.project.foodDelivery.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final com.project.foodDelivery.service.OrderService orderService;

    @Autowired
    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
        this.orderService = new com.project.foodDelivery.service.OrderService();
    }

    public Delivery assignDelivery(String orderId, String deliveryPersonId) {
        Delivery delivery = deliveryRepository.createDelivery(orderId, deliveryPersonId);
        // Update order status to indicate it's being delivered
        orderService.updateOrderStatus(orderId, "Delivering");
        return delivery;
    }

    public List<Delivery> getAllActiveDeliveries() {
        return deliveryRepository.getAllActiveDeliveries();
    }

    public List<Delivery> getDeliveryHistory() {
        return deliveryRepository.getDeliveryHistory();
    }

    public void updateDeliveryStatus(String deliveryId, String newStatus) {
        // Get the delivery before updating to access the orderId
        Delivery delivery = deliveryRepository.getDeliveryById(deliveryId);
        if (delivery != null) {
            // Update delivery status
            deliveryRepository.updateDeliveryStatus(deliveryId, newStatus);
            
            // Update corresponding order status
            String orderId = delivery.getOrderId();
            if (orderId != null) {
                // Map delivery statuses to order statuses
                String orderStatus;
                switch (newStatus) {
                    case "Picked Up":
                        orderStatus = "Picked Up";
                        break;
                    case "In Transit":
                        orderStatus = "In Transit";
                        break;
                    case "Delivered":
                        orderStatus = "Delivered";
                        break;
                    default:
                        orderStatus = newStatus;
                }
                orderService.updateOrderStatus(orderId, orderStatus);
            }
        }
    }

    public void removeCompletedDelivery(String deliveryId) {
        deliveryRepository.deleteCompletedDelivery(deliveryId);
    }
}