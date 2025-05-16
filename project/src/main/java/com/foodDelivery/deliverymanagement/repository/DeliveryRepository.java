package com.fooddelivery.deliverymanagement.repository;

import com.fooddelivery.deliverymanagement.model.Delivery;
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

    // Create a new delivery
    public Delivery createDelivery(String orderId, String deliveryPersonId) {
        Delivery delivery = new Delivery(
            UUID.randomUUID().toString(), 
            orderId, 
            deliveryPersonId
        );
        saveDelivery(delivery);
        return delivery;
    }

    // Save delivery to file
    private void saveDelivery(Delivery delivery) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DELIVERY_FILE, true))) {
            writer.write(delivery.toString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Read all active deliveries
    public List<Delivery> getAllActiveDeliveries() {
        return readDeliveriesFromFile().stream()
            .filter(d -> !d.getStatus().equals("Delivered"))
            .collect(Collectors.toList());
    }

    // Read delivery history
    public List<Delivery> getDeliveryHistory() {
        return readDeliveriesFromFile().stream()
            .filter(d -> d.getStatus().equals("Delivered"))
            .collect(Collectors.toList());
    }

    // Update delivery status
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

    // Delete completed delivery
    public void deleteCompletedDelivery(String deliveryId) {
        List<Delivery> deliveries = readDeliveriesFromFile().stream()
            .filter(d -> !d.getDeliveryId().equals(deliveryId))
            .collect(Collectors.toList());
        updateDeliveriesFile(deliveries);
    }

    // Read deliveries from file
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
                    
                    // Parse timestamps with more robust error handling
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
                        // Fallback to current time if parsing fails
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

    // Update entire deliveries file
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
