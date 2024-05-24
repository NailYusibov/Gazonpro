package com.gitlab.controller;

import com.gitlab.controllers.api.rest.ReviewImageRestApi;
import com.gitlab.dto.ReviewImageDto;
import com.gitlab.dto.ReviewImageUpdateDto;
import com.gitlab.model.Review;
import com.gitlab.model.ReviewImage;
import com.gitlab.service.ReviewImageService;
import com.gitlab.service.ReviewService;
import com.gitlab.util.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class ReviewImageController implements ReviewImageRestApi {

    private final ReviewImageService reviewImageService;
    private final ReviewService reviewService;

    public ResponseEntity<List<ReviewImageDto>> getPage(Integer page, Integer size) {
        var reviewImagePage = reviewImageService.getPageDto(page, size);
        if (reviewImagePage == null || reviewImagePage.getContent().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reviewImagePage.getContent());
    }

    @Override
    public ResponseEntity<ReviewImageDto> get(Long id) {
        return reviewImageService.findByIdDto(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<ReviewImageDto> create(ReviewImageDto reviewImageDto) {
        ReviewImageDto savedReviewImageDto = reviewImageService.saveDto(reviewImageDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedReviewImageDto);
    }

    @Override
    public ResponseEntity<ReviewImageDto> update(Long id, ReviewImageUpdateDto reviewImageUpdateDto) {
        Optional<ReviewImageDto> updatedReviewImageDto = reviewImageService.updateDto(id, reviewImageUpdateDto);
        return updatedReviewImageDto
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        Optional<ReviewImage> reviewImage = reviewImageService.delete(id);

        if (reviewImage.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().build();
        }
    }

    @Override
    public ResponseEntity<long[]> getImagesIDsByReviewId(Long id) {
        Optional<Review> reviewOptional = reviewService.findById(id);

        if (reviewOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (reviewOptional.get().getReviewImages().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        long[] images = reviewOptional.orElse(null).getReviewImages().stream()
                .map(ReviewImage::getId).mapToLong(Long::valueOf).toArray();
        return ResponseEntity.status(HttpStatus.OK)
                .body(images);
    }

    @Override
    public ResponseEntity<String> uploadImagesByReviewId(MultipartFile[] files, Long id) throws IOException {
        Optional<Review> reviewOptional = reviewService.findById(id);

        if (reviewOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("There is no reviewOptional with such id");
        }
        if (files.length == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("At least one file should be included");
        }

        List<ReviewImage> imageList = new ArrayList<>();
        for (MultipartFile file : files) {
            var image = new ReviewImage();
            image.setReview(reviewOptional.get());
            image.setName(file.getOriginalFilename());
            image.setData(ImageUtils.compressImage(file.getBytes()));
            imageList.add(image);
        }
        reviewImageService.saveAll(imageList);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<String> deleteAllImagesByReviewId(Long id) {
        Optional<Review> reviewOptional = reviewService.findById(id);
        if (reviewOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("There is no reviewOptional with such id");
        }
        if (reviewOptional.get().getReviewImages().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("reviewOptional with such id has no images");
        }

        reviewOptional.get().getReviewImages().stream()
                .map(ReviewImage::getId)
                .forEach(reviewImageService::delete);
        return ResponseEntity.ok().build();
    }
}