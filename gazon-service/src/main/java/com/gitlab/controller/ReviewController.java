package com.gitlab.controller;

import com.gitlab.controllers.api.rest.ReviewRestApi;
import com.gitlab.dto.ReviewDto;
import com.gitlab.model.Review;
import com.gitlab.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ReviewController implements ReviewRestApi {

    private final ReviewService reviewService;

    public ResponseEntity<List<ReviewDto>> getPage(Integer page, Integer size) {
        var reviewPage = reviewService.getPageDto(page, size);
        if (reviewPage == null || reviewPage.getContent().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reviewPage.getContent());
    }

    @Override
    public ResponseEntity<ReviewDto> get(Long id) {
        Optional<ReviewDto> reviewDtoOptional = reviewService.findByIdDto(id);

        return reviewDtoOptional.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<ReviewDto>> getAllReviewsForAProductByProductId(Long productId) {
        List<ReviewDto> reviewsDto = reviewService.findListOfAllReviewsByProductId(productId);
        return ResponseEntity.ok(reviewsDto);
    }

    public ResponseEntity<Long> getReviewAmount(Long id) {
        Long reviewAmount = reviewService.findByProductId(id);
        return ResponseEntity.ok(reviewAmount);
    }

    @Override
    public ResponseEntity<ReviewDto> create(ReviewDto reviewDto) {
        ReviewDto createdReviewDto = reviewService.saveDto(reviewDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReviewDto);
    }


    @Override
    public ResponseEntity<ReviewDto> update(Long id, ReviewDto reviewDto) {
        Optional<ReviewDto> updatedReviewDtoOptional = reviewService.updateDto(id, reviewDto);

        return updatedReviewDtoOptional.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        Optional<Review> reviewOptional = reviewService.delete(id);
        return reviewOptional.isEmpty() ?
                ResponseEntity.notFound().build() :
                ResponseEntity.ok().build();
    }
}