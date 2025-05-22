package com.project.foodDelivery.repository;

import com.project.foodDelivery.model.FoodItem;
import com.project.foodDelivery.model.NonVegItem;
import com.project.foodDelivery.model.VegItem;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository
public class FoodItemRepository {
    private static final String FILE_PATH = System.getProperty("user.dir") + "/food_items.txt";
    
    // Create a new food item or update an existing one
    public FoodItem save(FoodItem foodItem) {
        List<FoodItem> foodItems = findAll();
        
        // Generate a unique ID if not provided
        if (foodItem.getId() == null || foodItem.getId().isEmpty()) {
            foodItem.setId(UUID.randomUUID().toString());
            System.out.println("Generated new ID for food item: " + foodItem.getId());
        }
        
        // Check if item exists (for update)
        boolean exists = false;
        for (int i = 0; i < foodItems.size(); i++) {
            if (foodItems.get(i).getId().equals(foodItem.getId())) {
                FoodItem oldItem = foodItems.get(i);
                
                // Log the update for debugging
                System.out.println("Updating existing food item: " + foodItem.getId());
                System.out.println("Old image URL: " + oldItem.getImageUrl());
                System.out.println("New image URL: " + foodItem.getImageUrl());
                
                // Remove the old item (we'll replace it with the updated one)
                foodItems.remove(i);
                exists = true;
                break;
            }
        }
        
        if (exists) {
            System.out.println("Updating existing food item with ID: " + foodItem.getId());
        } else {
            System.out.println("Creating new food item with ID: " + foodItem.getId());
        }
        
        // Add the item to the list
        foodItems.add(foodItem);
        
        // Save all items back to file
        boolean saved = saveAll(foodItems);
        if (!saved) {
            System.err.println("Failed to save food items to file!");
        }
        
        return foodItem;
    }
    
    // Read all food items
    public List<FoodItem> findAll() {
        List<FoodItem> foodItems = new ArrayList<>();
        
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return foodItems;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    // Use a more robust approach to handle potential data issues
                    String[] parts = line.split(",", -1); // -1 to keep empty fields
                    if (parts.length >= 7) {
                        String id = parts[0].trim();
                        String name = parts[1].trim();
                        double price = 0.0;
                        try {
                            price = Double.parseDouble(parts[2].trim());
                        } catch (NumberFormatException e) {
                            // Default price if parsing fails
                            price = 0.0;
                        }
                        
                        String description = parts[3].trim();
                        String imageUrl = parts[4].trim();
                        boolean available = Boolean.parseBoolean(parts[5].trim());
                        String type = parts[6].trim();
                        
                        FoodItem item;
                        if ("veg".equals(type) && parts.length >= 8) {
                            boolean containsDairy = false;
                            try {
                                containsDairy = Boolean.parseBoolean(parts[7].trim());
                            } catch (Exception e) {
                                // Default to false if parsing fails
                            }
                            item = new VegItem(id, name, price, description, imageUrl, available, containsDairy);
                        } else if ("non-veg".equals(type) && parts.length >= 8) {
                            String meatType = parts.length > 7 ? parts[7].trim() : "chicken";
                            item = new NonVegItem(id, name, price, description, imageUrl, available, meatType);
                        } else {
                            item = new FoodItem(id, name, price, description, imageUrl, available, type);
                        }
                        
                        foodItems.add(item);
                    }
                } catch (Exception e) {
                    // Log the error but continue processing other items
                    System.err.println("Error parsing line: " + line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return foodItems;
    }
    
    // Find a food item by ID
    public FoodItem findById(String id) {
        List<FoodItem> foodItems = findAll();
        
        for (FoodItem item : foodItems) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        
        return null;
    }
    
    // Delete a food item by ID
    public boolean deleteById(String id) {
        List<FoodItem> foodItems = findAll();
        boolean removed = foodItems.removeIf(item -> item.getId().equals(id));
        
        if (removed) {
            saveAll(foodItems);
        }
        
        return removed;
    }
    
    // Save all food items to file
    private boolean saveAll(List<FoodItem> foodItems) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (FoodItem item : foodItems) {
                writer.write(item.toString());
                writer.newLine();
            }
            writer.flush();
            System.out.println("Successfully saved " + foodItems.size() + " food items to file");
            return true;
        } catch (IOException e) {
            System.err.println("Error saving food items to file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
