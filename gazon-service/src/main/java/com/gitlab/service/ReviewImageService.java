package com.gitlab.service;

import com.gitlab.dto.ReviewImageDto;
import com.gitlab.mapper.ReviewImageMapper;
import com.gitlab.model.ReviewImage;
import com.gitlab.repository.ReviewImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewImageService {

    private final ReviewImageRepository reviewImageRepository;

    private final ReviewImageMapper reviewImageMapper;

    public List<ReviewImage> findAll() {
        return reviewImageRepository.findAll();
    }

    public List<ReviewImageDto> findAllDto() {
        List<ReviewImage> reviewImages = reviewImageRepository.findAll();
        return reviewImages.stream()
                .map(reviewImageMapper::toDto)
                .collect(Collectors.toList());
    }


    public Optional<ReviewImage> findById(Long id) {
        return reviewImageRepository.findById(id);
    }

    public Optional<ReviewImageDto> findByIdDto(Long id) {
        Optional<ReviewImage> reviewImageOptional = reviewImageRepository.findById(id);
        return reviewImageOptional.map(reviewImageMapper::toDto);
    }

    @Transactional
    public ReviewImage save(ReviewImage reviewImage) {
        return reviewImageRepository.save(reviewImage);
    }

    @Transactional
    public ReviewImageDto saveDto(ReviewImageDto reviewImageDto) {
        ReviewImage reviewImage = reviewImageMapper.toEntity(reviewImageDto);
        ReviewImage savedReviewImage = reviewImageRepository.save(reviewImage);
        return reviewImageMapper.toDto(savedReviewImage);
    }

    @Transactional
    public Optional<ReviewImage> update(Long id, ReviewImage reviewImage) {
        Optional<ReviewImage> imageOptional = findById(id);
        ReviewImage currentReviewImage;
        if (imageOptional.isEmpty()) {
            return imageOptional;
        } else {
            currentReviewImage = imageOptional.get();
        }
        if (reviewImage.getName() != null) {
            currentReviewImage.setName(reviewImage.getName());
        }
        if (reviewImage.getData() != null) {
            currentReviewImage.setData(reviewImage.getData());
        }
        return Optional.of(reviewImageRepository.save(currentReviewImage));
    }

    @Transactional
    public Optional<ReviewImageDto> updateDto(Long id, ReviewImageDto reviewImageDto) {
        Optional<ReviewImage> imageOptional = findById(id);
        if (imageOptional.isEmpty()) {
            return Optional.empty();
        }

        ReviewImage currentReviewImage = imageOptional.get();

        if (reviewImageDto.getName() != null) {
            currentReviewImage.setName(reviewImageDto.getName());
        }
        if (reviewImageDto.getData() != null) {
            currentReviewImage.setData(reviewImageDto.getData());
        }

        ReviewImage updatedReviewImage = reviewImageRepository.save(currentReviewImage);

        return Optional.of(reviewImageMapper.toDto(updatedReviewImage));
    }

    @Transactional
    public Optional<ReviewImage> delete(Long id) {
        Optional<ReviewImage> imageOptional = findById(id);
        if (imageOptional.isPresent()) {
            reviewImageRepository.deleteById(id);
        }
        return imageOptional;
    }

    @Transactional
    public Optional<ReviewImageDto> deleteDto(Long id) {
        Optional<ReviewImage> imageOptional = findById(id);
        if (imageOptional.isPresent()) {
            reviewImageRepository.deleteById(id);
            return Optional.of(reviewImageMapper.toDto(imageOptional.get()));
        }
        return Optional.empty();
    }

    @Transactional
    public List<ReviewImage> saveAll(List<ReviewImage> imageList) {
        return reviewImageRepository.saveAll(imageList);
    }

    @Transactional
    public List<ReviewImage> saveAllDto(List<ReviewImageDto> imageDtoList) {
        List<ReviewImage> imageList = imageDtoList.stream()
                .map(reviewImageMapper::toEntity)
                .collect(Collectors.toList());
        return reviewImageRepository.saveAll(imageList);
    }
}