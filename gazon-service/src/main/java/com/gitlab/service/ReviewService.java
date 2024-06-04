package com.gitlab.service;

import com.gitlab.dto.ReviewDto;
import com.gitlab.enums.EntityStatus;
import com.gitlab.mapper.ReviewMapper;
import com.gitlab.mapper.ReviewMapperImpl;
import com.gitlab.model.Review;
import com.gitlab.repository.ReviewRepository;
import com.gitlab.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductService productService;
    private final ReviewMapper reviewMapper;
    private final ReviewMapperImpl reviewMapperImpl;
    private final UserRepository userRepository;

    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    public List<ReviewDto> findListOfAllReviewsByProductId(Long id) {

        if (productService.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Товар не найден");
        }

        List<Review> reviewList = reviewRepository.findAllByProductId(id);
        return reviewMapperImpl.toDtoList(reviewList);
    }

    public List<ReviewDto> findAllDto() {
        return findAll()
                .stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<Review> findById(Long id) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (reviewOptional.isPresent() && reviewOptional.get().getEntityStatus().equals(EntityStatus.ACTIVE)) {
            return reviewRepository.findById(id);
        }
        return Optional.empty();
    }

    public Optional<ReviewDto> findByIdDto(Long id) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (reviewOptional.isPresent() && reviewOptional.get().getEntityStatus().equals(EntityStatus.ACTIVE)) {
            return reviewOptional.map(reviewMapper::toDto);
        }
        return Optional.empty();
    }

    public Page<Review> getPage(Integer page, Integer size) {
        if (page == null || size == null) {
            var reviews = findAll();
            if (reviews.isEmpty()) {
                return Page.empty();
            }
            return new PageImpl<>(reviews);
        }
        if (page < 0 || size < 1) {
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        return reviewRepository.findAll(pageRequest);
    }

    public Page<ReviewDto> getPageDto(Integer page, Integer size) {

        if (page == null || size == null) {
            var reviews = findAllDto();
            if (reviews.isEmpty()) {
                return Page.empty();
            }
            return new PageImpl<>(reviews);
        }
        if (page < 0 || size < 1) {
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Review> reviewPage = reviewRepository.findAll(pageRequest);
        return reviewPage.map(reviewMapper::toDto);
    }

    @Transactional
    public Review save(Review review) {
        review.setEntityStatus(EntityStatus.ACTIVE);
        return reviewRepository.save(review);
    }

    @Transactional
    public ReviewDto saveDto(ReviewDto reviewDto) {
        Review review = reviewMapper.toEntity(reviewDto);
        review.setEntityStatus(EntityStatus.ACTIVE);
        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toDto(savedReview);
    }

    @Transactional
    public Optional<Review> update(Long id, Review review) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        Review currentReview;
        if (reviewOptional.isEmpty()) {
            return reviewOptional;
        } else {
            currentReview = reviewOptional.get();
            currentReview.setEntityStatus(EntityStatus.ACTIVE);
        }
        if (review.getPros() != null) {
            currentReview.setPros(review.getPros());
        }
        if (review.getCons() != null) {
            currentReview.setCons(review.getCons());
        }
        if (review.getComment() != null) {
            currentReview.setComment(review.getComment());
        }
        if (review.getRating() != null) {
            currentReview.setRating(review.getRating());
        }
        if (review.getHelpfulCounter() != null) {
            currentReview.setHelpfulCounter(review.getHelpfulCounter());
        }
        if (review.getNotHelpfulCounter() != null) {
            currentReview.setNotHelpfulCounter(review.getNotHelpfulCounter());
        }
        if (review.getUser() != null) {
            currentReview.setUser(review.getUser());
        }
        return Optional.of(reviewRepository.save(currentReview));
    }

    @Transactional
    public Optional<ReviewDto> updateDto(Long id, ReviewDto reviewDto) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (reviewOptional.isEmpty()) {
            return Optional.empty();
        }

        Review currentReview = reviewOptional.get();
        if (reviewDto.getPros() != null) {
            currentReview.setPros(reviewDto.getPros());
        }
        if (reviewDto.getCons() != null) {
            currentReview.setCons(reviewDto.getCons());
        }
        if (reviewDto.getComment() != null) {
            currentReview.setComment(reviewDto.getComment());
        }
        if (reviewDto.getRating() != null) {
            currentReview.setRating(reviewDto.getRating());
        }
        if (reviewDto.getHelpfulCounter() != null) {
            currentReview.setHelpfulCounter(reviewDto.getHelpfulCounter());
        }
        if (reviewDto.getNotHelpfulCounter() != null) {
            currentReview.setNotHelpfulCounter(reviewDto.getNotHelpfulCounter());
        }
        if (reviewDto.getUserId() != null) {
            currentReview.setUser(userRepository.findById(reviewDto.getUserId()).orElseThrow());
        }

        Review updatedReview = reviewRepository.save(currentReview);
        return Optional.of(reviewMapper.toDto(updatedReview));
    }

    @Transactional
    public Optional<Review> delete(Long id) {
        Optional<Review> optionalDeletedReview = reviewRepository.findById(id);
        if (optionalDeletedReview.isEmpty() || optionalDeletedReview.get().getEntityStatus().equals(EntityStatus.DELETED)) {
            return Optional.empty();
        }
        Review deletedReview = optionalDeletedReview.get();
        deletedReview.setEntityStatus(EntityStatus.DELETED);
        reviewRepository.save(deletedReview);
        return optionalDeletedReview;
    }

    public Long findByProductId(Long id) {
        if (productService.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Товар не найден");
        }

        return reviewRepository.countReviewByProduct_IdAndEntityStatus(id, EntityStatus.ACTIVE);
    }
}