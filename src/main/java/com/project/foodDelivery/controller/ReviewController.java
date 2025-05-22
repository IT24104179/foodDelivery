package com.project.foodDelivery.controller;

import com.project.foodDelivery.model.Review;
import com.project.foodDelivery.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;


    @GetMapping("/submit")
    public String showForm(Model model, 
                          @RequestParam(required = false) String targetType,
                          @RequestParam(required = false) String targetId,
                          @RequestParam(required = false) String orderId,
                          @RequestParam(required = false) String userEmail,
                          @RequestParam(required = false) String userName) {
        Review review = new Review();
        if (targetType != null) {
            review.setTargetType(targetType);
        }
        if (targetId != null) {
            review.setTargetId(targetId);
        }
        if (userEmail != null) {
            review.setUserEmail(userEmail);
        }
        if (userName != null) {
            review.setUserName(userName);
        }
        
        model.addAttribute("review", review);
        model.addAttribute("orderId", orderId);
        return "review/submit-review";
    }


    @PostMapping("/submit")
    public String submit(@ModelAttribute Review review, 
                        @RequestParam(required = false) String orderId,
                        RedirectAttributes redirectAttributes) {
        try {
            reviewService.submitReview(
                    review.getUserEmail(),
                    review.getUserName(),
                    review.getTargetType(),
                    review.getTargetId(),
                    review.getComment(),
                    review.getRating(),
                    orderId
            );
            redirectAttributes.addFlashAttribute("success", "Your review has been submitted successfully!");
            return "redirect:/customer-home";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error submitting review: " + e.getMessage());
            return "redirect:/reviews/submit?targetType=" + review.getTargetType() + 
                   "&targetId=" + review.getTargetId() + 
                   "&orderId=" + orderId + 
                   "&userEmail=" + review.getUserEmail() + 
                   "&userName=" + review.getUserName();
        }
    }


    @GetMapping("/view/{type}/{id}")
    public String viewReviews(@PathVariable("type") String targetType,
                              @PathVariable("id") String targetId,
                              Model model) {
        List<Review> reviews = reviewService.getReviewsForTarget(targetType, targetId);
        model.addAttribute("reviews", reviews);
        model.addAttribute("targetType", targetType);
        model.addAttribute("targetId", targetId);
        return "review/view-reviews";
    }


    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") String reviewId, Model model) {
        Review review = reviewService.getReviewById(reviewId);
        model.addAttribute("review", review);
        return "review/edit-review";
    }


    @PostMapping("/update")
    public String update(@ModelAttribute Review review, RedirectAttributes redirectAttributes) {
        try {
            reviewService.updateReview(review);
            redirectAttributes.addFlashAttribute("success", "Review updated successfully!");
            return "redirect:/reviews/view/" + review.getTargetType() + "/" + review.getTargetId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating review: " + e.getMessage());
            return "redirect:/reviews/edit/" + review.getReviewId();
        }
    }


    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") String reviewId, RedirectAttributes redirectAttributes) {
        try {
            Review review = reviewService.getReviewById(reviewId);
            String type = review.getTargetType();
            String target = review.getTargetId();
            reviewService.deleteReview(reviewId);
            redirectAttributes.addFlashAttribute("success", "Review deleted successfully!");
            return "redirect:/reviews/view/" + type + "/" + target;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting review: " + e.getMessage());
            return "redirect:/customer-home";
        }
    }


    @GetMapping("/admin/all")
    public String viewAllReviewsAdmin(Model model) {
        List<Review> allReviews = reviewService.getAllReviews();
        model.addAttribute("reviews", allReviews);
        return "review/moderate-reviews";
    }

    @GetMapping("/all")
    public String viewAllReviews(Model model) {
        List<Review> allReviews = reviewService.getAllReviews();
        model.addAttribute("reviews", allReviews);
        return "review/all-reviews";
    }
    

    @GetMapping("/api/{type}/{id}")
    @ResponseBody
    public List<Review> getReviewsApi(@PathVariable("type") String targetType,
                                     @PathVariable("id") String targetId) {
        return reviewService.getReviewsForTarget(targetType, targetId);
    }
    

    @GetMapping("/api/check")
    @ResponseBody
    public boolean hasUserReviewed(@RequestParam("email") String userEmail,
                                  @RequestParam("type") String targetType,
                                  @RequestParam("id") String targetId) {
        return reviewService.hasUserReviewedTarget(userEmail, targetType, targetId);
    }
    

    @GetMapping("/api/delivered-orders/{customerId}")
    @ResponseBody
    public List<com.project.foodDelivery.model.Order> getDeliveredOrders(@PathVariable("customerId") String customerId) {
        return reviewService.getDeliveredOrdersByCustomerId(customerId);
    }
    

    @GetMapping("/api/check-order")
    @ResponseBody
    public boolean hasUserReviewedOrder(@RequestParam("email") String userEmail,
                                       @RequestParam("orderId") String orderId) {
        return reviewService.hasUserReviewedOrder(userEmail, orderId);
    }
}
