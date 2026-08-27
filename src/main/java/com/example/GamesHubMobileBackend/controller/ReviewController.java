package com.example.GamesHubMobileBackend.controller;


import com.example.GamesHubMobileBackend.models.Review;
import com.example.GamesHubMobileBackend.services.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReviewController {

    private ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/reviews")
    public ResponseEntity getReviews() {
        var currentReviews = reviewService.getReviews();
        if (CollectionUtils.isEmpty(currentReviews)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(currentReviews);
    }

    @GetMapping("/reviews/game/{gameId}")
    public ResponseEntity getReviewsByGame(@PathVariable String gameId) {
        var reviews = reviewService.getReviewsByGameId(gameId);
        if (CollectionUtils.isEmpty(reviews)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/reviews")
    public ResponseEntity<Object> addReview(@RequestBody Review review) {
        if (review.getRating() < 1 || review.getRating() > 5) {
            return ResponseEntity.badRequest().body("Rating must be between 1 and 5.");
        }
        return ResponseEntity.ok(reviewService.saveReview(review));
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Object> deleteReview(@PathVariable String id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok("Review Have Been Deleted");
    }
}



