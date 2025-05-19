package com.project.foodDelivery.controller;

import com.project.foodDelivery.model.Delivery;
import com.project.foodDelivery.service.DeliveryService;
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

    @GetMapping("/assign")
    public String showDeliveryAssignmentPage(@RequestParam(value = "orderId", required = false) String orderId, Model model) {
        Delivery delivery = new Delivery();
        if (orderId != null) {
            delivery.setOrderId(orderId);
        }
        model.addAttribute("delivery", delivery);
        return "delivery-assign";
    }

    @PostMapping("/assign")
    public String assignDelivery(@RequestParam String orderId, 
                                 @RequestParam String deliveryPersonId, 
                                 Model model) {
        Delivery newDelivery = deliveryService.assignDelivery(orderId, deliveryPersonId);
        model.addAttribute("message", "Delivery assigned successfully!");
        return "redirect:/delivery/track";
    }

    @GetMapping("/track")
    public String showTrackDeliveryPage(Model model) {
        model.addAttribute("activeDeliveries", deliveryService.getAllActiveDeliveries());
        return "delivery-track";
    }

    @PostMapping("/update-status")
    public String updateDeliveryStatus(@RequestParam String deliveryId, 
                                       @RequestParam String newStatus, 
                                       Model model) {
        deliveryService.updateDeliveryStatus(deliveryId, newStatus);
        model.addAttribute("message", "Delivery status updated successfully!");
        return "redirect:/delivery/track";
    }

    @GetMapping("/history")
    public String showDeliveryHistoryPage(Model model) {
        model.addAttribute("deliveryHistory", deliveryService.getDeliveryHistory());
        return "delivery-history";
    }

    @GetMapping("/personnel-dashboard")
    public String showDeliveryPersonnelDashboard(Model model) {
        model.addAttribute("activeDeliveries", deliveryService.getAllActiveDeliveries());
        return "delivery-personnel-dashboard";
    }
} 