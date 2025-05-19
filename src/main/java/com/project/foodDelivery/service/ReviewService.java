package com.project.foodDelivery.service;

import com.project.foodDelivery.model.Review;
import com.project.foodDelivery.model.Order;
import com.project.foodDelivery.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private OrderService orderService;

    public void submitReview(String userEmail, String userName, String targetType, String targetId, String comment, int rating, String orderId) throws Exception {
        // Check if order exists and is delivered
        if (orderId != null && !orderId.isEmpty()) {
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                throw new Exception("Order not found");
            }
            if (!"Delivered".equals(order.getStatus())) {
                throw new Exception("Cannot review an order that hasn't been delivered yet");
            }
            // Verify the food item matches the order
            if (!order.getFoodItemId().equals(targetId)) {
                throw new Exception("The food item doesn't match the order");
            }
        } else {
            throw new Exception("Order ID is required to submit a review");
        }
        
        String id = UUID.randomUUID().toString().substring(0,6);
        Review review = new Review(id, userEmail, targetType, targetId, comment, rating);
        review.setUserName(userName);
        review.setReviewDate(java.time.LocalDate.now().toString());
        review.setOrderId(orderId);
        reviewRepository.save(review);
    }

    public List<Review> getReviewsForTarget(String targetType, String targetId) {
        return reviewRepository.findByTarget(targetType, targetId);
    }
    
    public List<Review> getReviewsByUser(String userEmail) {
        return reviewRepository.findByUserEmail(userEmail);
    }

    public Review getReviewById(String reviewId) {
        return reviewRepository.findById(reviewId);
    }

    public void updateReview(Review updatedReview) {
        reviewRepository.update(updatedReview);
    }

    public void deleteReview(String reviewId) {
        reviewRepository.delete(reviewId);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
    
    public boolean hasUserReviewedTarget(String userEmail, String targetType, String targetId) {
        List<Review> userReviews = getReviewsByUser(userEmail);
        for (Review review : userReviews) {
            if (review.getTargetType().equals(targetType) && review.getTargetId().equals(targetId)) {
                return true;
            }
        }
        return false;
    }
    
    public List<Order> getDeliveredOrdersByCustomerId(String customerId) {
        List<Order> allOrders = orderService.getOrdersByCustomerId(customerId);
        List<Order> deliveredOrders = new java.util.ArrayList<>();
        
        for (Order order : allOrders) {
            if ("Delivered".equals(order.getStatus())) {
                deliveredOrders.add(order);
            }
        }
        
        return deliveredOrders;
    }
    
    public boolean hasUserReviewedOrder(String userEmail, String orderId) {
        List<Review> userReviews = getReviewsByUser(userEmail);
        for (Review review : userReviews) {
            if (review.getOrderId() != null && review.getOrderId().equals(orderId)) {
                return true;
            }
        }
        return false;
    }
}
