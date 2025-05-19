package com.project.foodDelivery.service;

import com.project.foodDelivery.model.Order;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OrderFileHandler {
    private static final String FILE_PATH = "orders.txt";

    public static void saveOrder(Order order) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(order.toFileString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Order> readAllOrders() {
        List<Order> orders = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                Order order = Order.fromFileString(line);
                if (order != null) orders.add(order);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public static List<Order> findOrdersByCustomerId(String customerId) {
        List<Order> result = new ArrayList<>();
        for (Order o : readAllOrders()) {
            if (o.getCustomerId().equals(customerId)) result.add(o);
        }
        return result;
    }

    public static void updateOrder(Order updatedOrder) {
        List<Order> orders = readAllOrders();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Order order : orders) {
                if (order.getId().equals(updatedOrder.getId())) {
                    writer.write(updatedOrder.toFileString());
                } else {
                    writer.write(order.toFileString());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Order findOrderById(String id) {
        for (Order o : readAllOrders()) {
            if (o.getId().equals(id)) return o;
        }
        return null;
    }
} 