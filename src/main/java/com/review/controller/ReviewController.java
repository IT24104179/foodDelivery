package com.review.controller;

import com.review.model.Review;
import com.review.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Show submission form
    @GetMapping("/submit")
    public String showForm(Model model) {
        model.addAttribute("review", new Review());
        return "submit-review";
    }

    // Handle form submission
    @PostMapping("/submit")
    public String submit(@ModelAttribute Review review) {
        reviewService.submitReview(
                review.getUserEmail(),
                review.getTargetType(),
                review.getTargetId(),
                review.getComment(),
                review.getRating()
        );
        return "redirect:/reviews/view/" + review.getTargetType() + "/" + review.getTargetId();
    }

    // View reviews for a specific target
    @GetMapping("/view/{type}/{id}")
    public String viewReviews(@PathVariable("type") String targetType,
                              @PathVariable("id") String targetId,
                              Model model) {
        List<Review> reviews = reviewService.getReviewsForTarget(targetType, targetId);
        model.addAttribute("reviews", reviews);
        model.addAttribute("targetType", targetType);
        model.addAttribute("targetId", targetId);
        return "view-reviews";
    }

    // Show edit form
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") String reviewId, Model model) {
        Review review = reviewService.getReviewById(reviewId);
        model.addAttribute("review", review);
        return "edit-review";
    }

    // Handle edit form submission
    @PostMapping("/update")
    public String update(@ModelAttribute Review review) {
        reviewService.updateReview(review);
        return "redirect:/reviews/view/" + review.getTargetType() + "/" + review.getTargetId();
    }

    // Delete a review
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") String reviewId) {
        Review review = reviewService.getReviewById(reviewId);
        String type = review.getTargetType();
        String target = review.getTargetId();
        reviewService.deleteReview(reviewId);
        return "redirect:/reviews/view/" + type + "/" + target;
    }

    // Admin: view all
    @GetMapping("/admin/all")
    public String viewAllReviewsAdmin(Model model) {
        List<Review> allReviews = reviewService.getAllReviews();
        model.addAttribute("reviews", allReviews);
        return "moderate-reviews";
    }

    @GetMapping("/all")
    public String viewAllReviews(Model model) {
        List<Review> allReviews = reviewService.getAllReviews();
        model.addAttribute("reviews", allReviews);
        return "all-reviews";
    }
}
