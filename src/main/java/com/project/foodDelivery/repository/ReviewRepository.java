package com.project.foodDelivery.repository;

import com.project.foodDelivery.model.Review;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReviewRepository {
    private static final String DIRECTORY = "data";
    private static final String FILE_PATH = DIRECTORY + "/reviews.txt";
    private static final String DELIMITER = "::";

    public ReviewRepository() {
        File dir = new File(DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(FILE_PATH);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Review> findAll() {
        List<Review> reviews = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Review review = parseReview(line);
                if (review != null) reviews.add(review);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public List<Review> findByTarget(String targetType, String targetId) {
        List<Review> result = new ArrayList<>();
        for (Review review : findAll()) {
            if (review.getTargetType().equalsIgnoreCase(targetType)
                    && review.getTargetId().equalsIgnoreCase(targetId)) {
                result.add(review);
            }
        }
        return result;
    }
    
    public List<Review> findByUserEmail(String userEmail) {
        List<Review> result = new ArrayList<>();
        for (Review review : findAll()) {
            if (review.getUserEmail().equalsIgnoreCase(userEmail)) {
                result.add(review);
            }
        }
        return result;
    }

    public Review findById(String reviewId) {
        for (Review review : findAll()) {
            if (review.getReviewId().equals(reviewId)) {
                return review;
            }
        }
        return null;
    }

    public void save(Review review) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(formatReview(review));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(Review updatedReview) {
        List<Review> reviews = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Review review : reviews) {
                if (review.getReviewId().equals(updatedReview.getReviewId())) {
                    writer.write(formatReview(updatedReview));
                } else {
                    writer.write(formatReview(review));
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete(String reviewId) {
        List<Review> reviews = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Review review : reviews) {
                if (!review.getReviewId().equals(reviewId)) {
                    writer.write(formatReview(review));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formatReview(Review r) {
        return String.join(DELIMITER,
                r.getReviewId(),
                r.getUserEmail(),
                r.getTargetType(),
                r.getTargetId(),
                r.getComment().replace("\n", " "),
                String.valueOf(r.getRating()),
                r.getReviewDate() != null ? r.getReviewDate() : java.time.LocalDate.now().toString(),
                r.getUserName() != null ? r.getUserName() : ""
        );
    }

    private Review parseReview(String line) {
        String[] parts = line.split(DELIMITER);
        if (parts.length < 6) return null;
        
        Review review = new Review(
                parts[0],
                parts[1],
                parts[2],
                parts[3],
                parts[4],
                Integer.parseInt(parts[5])
        );
        
        if (parts.length > 6) {
            review.setReviewDate(parts[6]);
        }
        
        if (parts.length > 7) {
            review.setUserName(parts[7]);
        }
        
        return review;
    }
}
