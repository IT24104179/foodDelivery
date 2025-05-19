package com.project.foodDelivery.repository;

import com.project.foodDelivery.model.Delivery;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class DeliveryRepository {
    private static final String DELIVERY_FILE = "deliveries.txt";

    public Delivery createDelivery(String orderId, String deliveryPersonId) {
        Delivery delivery = new Delivery(
            UUID.randomUUID().toString(), 
            orderId, 
            deliveryPersonId
        );
        saveDelivery(delivery);
        return delivery;
    }

    private void saveDelivery(Delivery delivery) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DELIVERY_FILE, true))) {
            writer.write(delivery.toString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Delivery> getAllActiveDeliveries() {
        return readDeliveriesFromFile().stream()
            .filter(d -> !d.getStatus().equals("Delivered"))
            .collect(Collectors.toList());
    }

    public List<Delivery> getDeliveryHistory() {
        return readDeliveriesFromFile().stream()
            .filter(d -> d.getStatus().equals("Delivered"))
            .collect(Collectors.toList());
    }
    
    public Delivery getDeliveryById(String deliveryId) {
        return readDeliveriesFromFile().stream()
            .filter(d -> d.getDeliveryId().equals(deliveryId))
            .findFirst()
            .orElse(null);
    }

    public void updateDeliveryStatus(String deliveryId, String newStatus) {
        List<Delivery> deliveries = readDeliveriesFromFile();
        deliveries.stream()
            .filter(d -> d.getDeliveryId().equals(deliveryId))
            .findFirst()
            .ifPresent(delivery -> {
                delivery.setStatus(newStatus);
                if (newStatus.equals("Delivered")) {
                    delivery.setDeliveryTime(LocalDateTime.now());
                }
                updateDeliveriesFile(deliveries);
            });
    }

    public void deleteCompletedDelivery(String deliveryId) {
        List<Delivery> deliveries = readDeliveriesFromFile().stream()
            .filter(d -> !d.getDeliveryId().equals(deliveryId))
            .collect(Collectors.toList());
        updateDeliveriesFile(deliveries);
    }

    private List<Delivery> readDeliveriesFromFile() {
        List<Delivery> deliveries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DELIVERY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    Delivery delivery = new Delivery();
                    delivery.setDeliveryId(parts[0]);
                    delivery.setOrderId(parts[1]);
                    delivery.setDeliveryPersonId(parts[2]);
                    delivery.setStatus(parts[3]);
                    try {
                        if (!"N/A".equals(parts[4])) {
                            delivery.setAssignedTime(LocalDateTime.parse(parts[4]));
                        } else {
                            delivery.setAssignedTime(LocalDateTime.now());
                        }
                        if (!"N/A".equals(parts[5])) {
                            delivery.setDeliveryTime(LocalDateTime.parse(parts[5]));
                        }
                    } catch (Exception e) {
                        delivery.setAssignedTime(LocalDateTime.now());
                        System.err.println("Error parsing timestamp: " + e.getMessage());
                    }
                    deliveries.add(delivery);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return deliveries;
    }

    private void updateDeliveriesFile(List<Delivery> deliveries) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DELIVERY_FILE))) {
            for (Delivery delivery : deliveries) {
                writer.write(delivery.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 