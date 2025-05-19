package com.project.foodDelivery.controller;

import com.project.foodDelivery.model.Order;
import com.project.foodDelivery.model.Payment;
import com.project.foodDelivery.service.OrderService;
import com.project.foodDelivery.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService = new OrderService();
    
    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public Order placeOrder(@RequestBody OrderRequest req) {
        return orderService.placeOrder(req.foodItemId, req.foodName, req.quantity, req.address, req.notes, req.customerId, req.customerName);
    }

    @GetMapping("/customer/{customerId}")
    public List<Order> getOrdersByCustomer(@PathVariable String customerId) {
        return orderService.getOrdersByCustomerId(customerId);
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable String id) {
        return orderService.getOrderById(id);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PutMapping("/{id}/status")
    public String updateOrderStatus(@PathVariable String id, @RequestBody StatusRequest req) {
        orderService.updateOrderStatus(id, req.status);
        return "Order status updated";
    }

    // DTO for order request
    public static class OrderRequest {
        public String foodItemId;
        public String foodName;
        public int quantity;
        public String address;
        public String notes;
        public String customerId;
        public String customerName;
    }

    public static class StatusRequest {
        public String status;
    }
    
    @GetMapping("/{orderId}/payments")
    public List<Payment> getPaymentsForOrder(@PathVariable String orderId) {
        // Return payment details for the given order ID
        return paymentService.getPaymentsByOrderId(orderId);
    }
}