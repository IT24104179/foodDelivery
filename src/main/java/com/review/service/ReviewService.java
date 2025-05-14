package com.review.service;

import com.review.model.Review;
import com.review.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public void submitReview(String userEmail, String targetType, String targetId, String comment, int rating) {
        String id = UUID.randomUUID().toString().substring(0,6);
        Review review = new Review(id, userEmail, targetType, targetId, comment, rating);
        reviewRepository.save(review);
    }

    public List<Review> getReviewsForTarget(String targetType, String targetId) {
        return reviewRepository.findByTarget(targetType, targetId);
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
}
