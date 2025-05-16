package com.fooddelivery.deliverymanagement.controller;

import com.fooddelivery.deliverymanagement.model.Delivery;
import com.fooddelivery.deliverymanagement.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/delivery")
public class DeliveryController {
    private final DeliveryService deliveryService;

    @Autowired
    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    // Delivery Assignment Page
    @GetMapping("/assign")
    public String showDeliveryAssignmentPage(Model model) {
        model.addAttribute("delivery", new Delivery());
        return "delivery-assign";
    }

    // Assign Delivery
    @PostMapping("/assign")
    public String assignDelivery(@RequestParam String orderId, 
                                 @RequestParam String deliveryPersonId, 
                                 Model model) {
        Delivery newDelivery = deliveryService.assignDelivery(orderId, deliveryPersonId);
        model.addAttribute("message", "Delivery assigned successfully!");
        return "redirect:/delivery/track";
    }

    // Track Delivery Page
    @GetMapping("/track")
    public String showTrackDeliveryPage(Model model) {
        model.addAttribute("activeDeliveries", deliveryService.getAllActiveDeliveries());
        return "delivery-track";
    }

    // Update Delivery Status
    @PostMapping("/update-status")
    public String updateDeliveryStatus(@RequestParam String deliveryId, 
                                       @RequestParam String newStatus, 
                                       Model model) {
        deliveryService.updateDeliveryStatus(deliveryId, newStatus);
        model.addAttribute("message", "Delivery status updated successfully!");
        return "redirect:/delivery/track";
    }

    // Delivery History Page
    @GetMapping("/history")
    public String showDeliveryHistoryPage(Model model) {
        model.addAttribute("deliveryHistory", deliveryService.getDeliveryHistory());
        return "delivery-history";
    }

    // Delivery Personnel Dashboard
    @GetMapping("/personnel-dashboard")
    public String showDeliveryPersonnelDashboard(Model model) {
        model.addAttribute("activeDeliveries", deliveryService.getAllActiveDeliveries());
        return "delivery-personnel-dashboard";
    }
}
