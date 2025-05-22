package com.project.foodDelivery.controller;

import com.project.foodDelivery.model.Order;
import com.project.foodDelivery.model.Payment;
import com.project.foodDelivery.service.OrderService;
import com.project.foodDelivery.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public String viewPayments(Model model) {
        // Role check is done client-side with JavaScript
        model.addAttribute("payments", paymentService.getAllPayments());
        return "payment/list";
    }

    @Autowired
    private OrderService orderService;
    
    @GetMapping("/new")
    public String showPaymentForm(@RequestParam(value = "orderId", required = false) String orderId,
                                  @RequestParam(value = "amount", required = false) Double amount,
                                  @RequestParam(value = "email", required = false) String email,
                                  Model model) {
        Payment payment = new Payment();
        
        if (orderId != null) {
            payment.setOrderId(orderId);
            
            // Try to get the order and use its totalAmount if available
            try {
                Order order = orderService.getOrderById(orderId);
                if (order != null && amount == null) {
                    // Use the order's totalAmount if no amount was provided
                    payment.setAmount(order.getTotalAmount());
                    System.out.println("Using order totalAmount: " + order.getTotalAmount() + " for payment");
                    
                    // Also set the customer email if available
                    if (email == null && order.getCustomerId() != null) {
                        payment.setCustomerEmail(order.getCustomerId());
                    }
                }
            } catch (Exception e) {
                System.err.println("Error getting order for payment: " + e.getMessage());
            }
        }
        
        // These will override values from the order if provided
        if (amount != null) {
            payment.setAmount(amount);
        }
        
        if (email != null) {
            payment.setCustomerEmail(email);
        }
        
        model.addAttribute("payment", payment);
        return "payment/form";
    }

    @PostMapping("/save")
    public String savePayment(@ModelAttribute Payment payment, RedirectAttributes redirectAttributes) {
        try {
            paymentService.createPayment(payment);
            redirectAttributes.addFlashAttribute("success", "Payment processed successfully!");
            
            // Redirect customers to customer home page
            return "redirect:/customer-home";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/payments/new";
        }
    }

    @GetMapping("/{id}")
    public String viewSinglePayment(@PathVariable("id") String id, Model model) {
        // Role check is done client-side with JavaScript
        paymentService.getPaymentById(id).ifPresent(p -> model.addAttribute("payment", p));
        return "payment/view";
    }
    
    @GetMapping("/order/{orderId}")
    public String viewPaymentsByOrder(@PathVariable("orderId") String orderId, Model model) {
        // Role check is done client-side with JavaScript
        model.addAttribute("payments", paymentService.getPaymentsByOrderId(orderId));
        model.addAttribute("orderId", orderId);
        return "payment/list";
    }
    
    @GetMapping("/process-payment")
    @ResponseBody
    public String checkPaymentStatus(@RequestParam("orderId") String orderId) {
        // This endpoint can be accessed by both customers and owners
        boolean hasPayment = paymentService.hasPayment(orderId);
        return hasPayment ? "paid" : "unpaid";
    }
    
    @GetMapping("/api/order/{orderId}")
    @ResponseBody
    public List<Payment> getPaymentsByOrderIdApi(@PathVariable("orderId") String orderId) {
        // Role check is done client-side with JavaScript
        return paymentService.getPaymentsByOrderId(orderId);
    }
    

}
