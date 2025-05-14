package com.review.model;

public class Review {
    private String reviewId;
    private String userEmail;
    private String targetType; // "restaurant" or "delivery"
    private String targetId;   // restaurantId or deliveryPersonId
    private String comment;
    private int rating;

    public Review() {}

    public Review(String reviewId, String userEmail, String targetType, String targetId, String comment, int rating) {
        this.reviewId = reviewId;
        this.userEmail = userEmail;
        this.targetType = targetType;
        this.targetId = targetId;
        this.comment = comment;
        this.rating = rating;
    }

    // Getters and setters
    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
}
