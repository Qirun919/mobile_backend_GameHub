package com.example.GamesHubMobileBackend.services;

import com.example.GamesHubMobileBackend.models.Review;
import com.example.GamesHubMobileBackend.repositories.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> getReviews() {
        return reviewRepository.findAll();
    }

    public List<Review> getReviewsByGameId(String gameId) {
        return reviewRepository.findByGameId(gameId);
    }

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    public boolean deleteReview(String id) {
        reviewRepository.deleteById(id);
        return true;
    }
}