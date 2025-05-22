package com.project.foodDelivery.controller;

import com.project.foodDelivery.model.FoodItem;
import com.project.foodDelivery.service.FoodItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/menu")
public class FoodItemController {

    @Autowired
    private FoodItemService foodItemService;
    
    // Display menu dashboard with all food items
    @GetMapping
    public String showMenu(Model model) {
        model.addAttribute("foodItems", foodItemService.getAllFoodItems());
        return "menu";
    }
    
    // Show form to add a new food item
    @GetMapping("/add")
    public String showAddFoodForm(Model model) {
        model.addAttribute("foodItem", new FoodItem());
        return "add_food";
    }
    
    // Process form submission to add a new food item
    @PostMapping("/add")
    public String addFoodItem(@RequestParam("name") String name,
                             @RequestParam("price") double price,
                             @RequestParam("description") String description,
                             @RequestParam("image") MultipartFile imageFile,
                             @RequestParam("available") boolean available,
                             @RequestParam("type") String type,
                             @RequestParam(value = "additionalInfo", required = false) String additionalInfo,
                             RedirectAttributes redirectAttributes) {
        try {
            foodItemService.createFoodItem(name, price, description, imageFile, available, type, additionalInfo);
            redirectAttributes.addFlashAttribute("successMessage", "Food item added successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload image: " + e.getMessage());
        }
        return "redirect:/menu";
    }
    
    // Show form to edit an existing food item
    @GetMapping("/edit/{id}")
    public String showEditFoodForm(@PathVariable("id") String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            FoodItem foodItem = foodItemService.getFoodItemById(id);
            if (foodItem == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Food item not found!");
                return "redirect:/menu";
            }
            model.addAttribute("foodItem", foodItem);
            return "edit_food";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error loading food item: " + e.getMessage());
            return "redirect:/menu";
        }
    }
    
    // Process form submission to update a food item
    @PostMapping("/edit/{id}")
    public String updateFoodItem(@PathVariable("id") String id,
                                @RequestParam("name") String name,
                                @RequestParam("price") double price,
                                @RequestParam("description") String description,
                                @RequestParam("image") MultipartFile imageFile,
                                @RequestParam("available") boolean available,
                                @RequestParam("type") String type,
                                @RequestParam(value = "additionalInfo", required = false) String additionalInfo,
                                RedirectAttributes redirectAttributes) {
        try {
            foodItemService.updateFoodItem(id, name, price, description, imageFile, available, type, additionalInfo);
            redirectAttributes.addFlashAttribute("successMessage", "Food item updated successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update food item: " + e.getMessage());
        }
        return "redirect:/menu";
    }
    
    // Delete a food item
    @GetMapping("/delete/{id}")
    public String deleteFoodItem(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        boolean deleted = foodItemService.deleteFoodItem(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Food item deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete food item.");
        }
        return "redirect:/menu";
    }
}
