package com.fooddelivery.orderbackend.service;

import com.fooddelivery.orderbackend.model.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private static final String FILE_PATH = "orders.txt";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private long currentId = 1;

    public synchronized Order saveOrder(Order order) {
        order.setTime(LocalDateTime.now());
        order.setOrderId(currentId++);
        List<Order> orders = getAllOrders();
        orders.add(order);
        writeOrdersToFile(orders);
        return order;
    }

    public List<Order> getAllOrders() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try {
            CollectionType typeRef = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, Order.class);
            return objectMapper.readValue(file, typeRef);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Order getOrderById(Long id) {
        return getAllOrders().stream()
                .filter(order -> order.getOrderId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void deleteOrder(Long id) {
        List<Order> orders = getAllOrders();
        orders.removeIf(order -> order.getOrderId().equals(id));
        writeOrdersToFile(orders);
    }

    public Order updateOrder(Long id, Order updatedOrder) {
        List<Order> orders = getAllOrders();
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderId().equals(id)) {
                updatedOrder.setOrderId(id);
                updatedOrder.setTime(LocalDateTime.now());
                orders.set(i, updatedOrder);
                writeOrdersToFile(orders);
                return updatedOrder;
            }
        }
        return null;
    }

    private void writeOrdersToFile(List<Order> orders) {
        try {
            objectMapper.writeValue(new File(FILE_PATH), orders);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
