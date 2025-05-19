package com.project.foodDelivery.model;

public class Review {
    private String reviewId;
    private String userEmail;
    private String targetType; // "restaurant" or "food"
    private String targetId;   // restaurantId or foodItemId
    private String comment;
    private int rating;
    private String reviewDate;
    private String userName;
    private String orderId;    // ID of the order this review is for

    public Review() {}

    public Review(String reviewId, String userEmail, String targetType, String targetId, String comment, int rating) {
        this.reviewId = reviewId;
        this.userEmail = userEmail;
        this.targetType = targetType;
        this.targetId = targetId;
        this.comment = comment;
        this.rating = rating;
        this.reviewDate = java.time.LocalDate.now().toString();
        this.orderId = null;
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
    
    public String getReviewDate() { return reviewDate; }
    public void setReviewDate(String reviewDate) { this.reviewDate = reviewDate; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
}
