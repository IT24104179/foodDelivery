package com.project.foodDelivery.service;

import com.project.foodDelivery.model.FoodItem;
import com.project.foodDelivery.model.NonVegItem;
import com.project.foodDelivery.model.VegItem;
import com.project.foodDelivery.repository.FoodItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class FoodItemService {

    @Autowired
    private FoodItemRepository foodItemRepository;
    
    // Use a more reliable path for uploads
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";

    // Get all food items
    public List<FoodItem> getAllFoodItems() {
        return foodItemRepository.findAll();
    }
    
    // Get food item by ID
    public FoodItem getFoodItemById(String id) {
        return foodItemRepository.findById(id);
    }
    
    // Create a new food item with image upload
    public FoodItem createFoodItem(String name, double price, String description, 
                                  MultipartFile imageFile, boolean available, 
                                  String type, String additionalInfo) throws IOException {
        
        // Handle image upload
        String imageUrl = "";
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = saveImage(imageFile);
        }
        
        FoodItem foodItem;
        String newId = UUID.randomUUID().toString();
        
        // Create appropriate food item based on type
        if ("veg".equals(type)) {
            boolean containsDairy = false;
            if (additionalInfo != null) {
                containsDairy = "true".equals(additionalInfo.toLowerCase()) || "on".equals(additionalInfo.toLowerCase());
            }
            foodItem = new VegItem(newId, name, price, description, imageUrl, available, containsDairy);
            System.out.println("Created new VegItem with ID: " + newId + ", containsDairy=" + containsDairy);
        } else if ("non-veg".equals(type)) {
            // Default to chicken if no meat type is provided
            String meatType = (additionalInfo != null && !additionalInfo.isEmpty()) ? additionalInfo : "chicken";
            foodItem = new NonVegItem(newId, name, price, description, imageUrl, available, meatType);
            System.out.println("Created new NonVegItem with ID: " + newId + ", meatType=" + meatType);
        } else {
            foodItem = new FoodItem(newId, name, price, description, imageUrl, available, type);
            System.out.println("Created new generic FoodItem with ID: " + newId + ", type=" + type);
        }
        
        return foodItemRepository.save(foodItem);
    }
    
    // Update an existing food item
    public FoodItem updateFoodItem(String id, String name, double price, String description, 
                                  MultipartFile imageFile, boolean available, 
                                  String type, String additionalInfo) throws IOException {
        
        FoodItem existingItem = foodItemRepository.findById(id);
        if (existingItem == null) {
            System.err.println("Cannot update food item: Item with ID " + id + " not found");
            return null;
        }
        
        // Handle image upload if a new image is provided
        String imageUrl = existingItem.getImageUrl();
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Always delete old image if it exists before saving the new one
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    System.out.println("Updating image for item " + id);
                    System.out.println("Old image URL: " + imageUrl);
                    boolean deleted = deleteImage(imageUrl);
                    if (deleted) {
                        System.out.println("Successfully deleted old image file");
                    } else {
                        System.err.println("Failed to delete old image file or file not found");
                    }
                }
                
                // Save the new image
                imageUrl = saveImage(imageFile);
                if (imageUrl == null || imageUrl.isEmpty()) {
                    // If saving the new image failed, keep the old URL
                    imageUrl = existingItem.getImageUrl();
                    System.err.println("Failed to save new image, keeping existing URL: " + imageUrl);
                } else {
                    System.out.println("New image URL for item " + id + ": " + imageUrl);
                }
            } catch (Exception e) {
                System.err.println("Error processing image: " + e.getMessage());
                e.printStackTrace();
                // Keep the existing image URL in case of error
                imageUrl = existingItem.getImageUrl();
            }
        } else {
            System.out.println("No new image provided for item " + id + ", keeping existing URL: " + imageUrl);
        }
        
        FoodItem updatedItem;
        
        // Create appropriate food item based on type
        if ("veg".equals(type)) {
            boolean containsDairy = false;
            if (additionalInfo != null) {
                containsDairy = "true".equals(additionalInfo.toLowerCase()) || "on".equals(additionalInfo.toLowerCase());
            }
            updatedItem = new VegItem(id, name, price, description, imageUrl, available, containsDairy);
            System.out.println("Updated VegItem with containsDairy=" + containsDairy);
        } else if ("non-veg".equals(type)) {
            // Default to chicken if no meat type is provided
            String meatType = (additionalInfo != null && !additionalInfo.isEmpty()) ? additionalInfo : "chicken";
            updatedItem = new NonVegItem(id, name, price, description, imageUrl, available, meatType);
            System.out.println("Updated NonVegItem with meatType=" + meatType);
        } else {
            updatedItem = new FoodItem(id, name, price, description, imageUrl, available, type);
            System.out.println("Updated generic FoodItem with type=" + type);
        }
        
        // Save the updated item to the repository
        FoodItem savedItem = foodItemRepository.save(updatedItem);
        System.out.println("Successfully updated food item " + id + " with image URL: " + savedItem.getImageUrl());
        return savedItem;
    }
    
    // Delete a food item
    public boolean deleteFoodItem(String id) {
        FoodItem item = foodItemRepository.findById(id);
        if (item != null && item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            deleteImage(item.getImageUrl());
        }
        return foodItemRepository.deleteById(id);
    }
    
    // Save uploaded image and return the file path
    private String saveImage(MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            System.out.println("No image file provided or empty file");
            return "";
        }
        
        // Create uploads directory if it doesn't exist
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            try {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    System.err.println("Failed to create upload directory: " + uploadDir.getAbsolutePath());
                    // Try to create parent directories if needed
                    File parentDir = uploadDir.getParentFile();
                    if (parentDir != null && !parentDir.exists()) {
                        boolean parentCreated = parentDir.mkdirs();
                        System.out.println("Attempted to create parent directory: " + (parentCreated ? "success" : "failed"));
                        // Try again to create the upload directory
                        created = uploadDir.mkdirs();
                        System.out.println("Second attempt to create upload directory: " + (created ? "success" : "failed"));
                    }
                } else {
                    System.out.println("Created upload directory: " + uploadDir.getAbsolutePath());
                }
            } catch (Exception e) {
                System.err.println("Error creating upload directory: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // Generate a unique filename
        String originalFilename = imageFile.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            originalFilename = "image.jpg";
        }
        
        // Remove any path information from the filename (for security)
        originalFilename = new File(originalFilename).getName();
        
        // Sanitize filename and make it unique
        String filename = UUID.randomUUID().toString() + "_" + originalFilename;
        Path filePath = Paths.get(UPLOAD_DIR, filename).toAbsolutePath();
        
        System.out.println("Saving image to: " + filePath);
        
        try {
            // Create parent directories if they don't exist
            Files.createDirectories(filePath.getParent());
            
            // Save the file
            Files.write(filePath, imageFile.getBytes());
            
            // Verify the file was saved
            if (Files.exists(filePath)) {
                System.out.println("Successfully saved image: " + filename + ", size: " + Files.size(filePath) + " bytes");
                System.out.println("Image saved at: " + filePath.toString());
                
                // Return the URL path that uses our custom image controller
                return "/image/" + filename;
            } else {
                System.err.println("Failed to save image: " + filename);
                return "";
            }
        } catch (Exception e) {
            System.err.println("Error saving image: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }
    
    // Delete image file
    private boolean deleteImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                // Extract filename from URL path
                String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                Path filePath = Paths.get(UPLOAD_DIR, filename).toAbsolutePath();
                
                System.out.println("Attempting to delete image file: " + filePath);
                
                // Check if file exists before trying to delete
                if (Files.exists(filePath)) {
                    boolean deleted = Files.deleteIfExists(filePath);
                    if (deleted) {
                        System.out.println("Successfully deleted image file: " + filename);
                        return true;
                    } else {
                        System.err.println("Failed to delete image file: " + filename);
                        return false;
                    }
                } else {
                    System.err.println("Image file not found: " + filePath);
                    return false;
                }
            } catch (IOException e) {
                System.err.println("Error deleting image file: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
