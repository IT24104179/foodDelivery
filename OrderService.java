package com.fooddelivery.orderbackend.service;

import com.fooddelivery.orderbackend.model.Order;
import com.fooddelivery.orderbackend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import the annotation
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Transactional // Add this annotation!
    public Order saveOrder(Order order) {
        order.setTime(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true) // Add this for read-only transactions
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        return order.orElse(null);
    }

    @Transactional // Add this annotation for delete operations
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Transactional
    public Order updateOrder(Long id, Order updatedOrder) {
        Optional<Order> existingOrder = orderRepository.findById(id);
        if (existingOrder.isPresent()) {
            Order orderToUpdate = existingOrder.get();
            orderToUpdate.setUserId(updatedOrder.getUserId());
            orderToUpdate.setTotalPrice(updatedOrder.getTotalPrice());
            orderToUpdate.setTime(LocalDateTime.now());
            orderToUpdate.setFoodItems(updatedOrder.getFoodItems());
            return orderRepository.save(orderToUpdate);
        } else {
            return null;
        }
    }
}