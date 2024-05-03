package com.gitlab.service;

import com.gitlab.dto.ReviewImageDto;
import com.gitlab.dto.ReviewImageUpdateDto;
import com.gitlab.mapper.ReviewImageMapper;
import com.gitlab.model.ReviewImage;
import com.gitlab.repository.ReviewImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        return reviewImageMapper.toDtoList(reviewImages);
    }

    public Optional<ReviewImage> findById(Long id) {
        return reviewImageRepository.findById(id);
    }

    public Optional<ReviewImageDto> findByIdDto(Long id) {
        Optional<ReviewImage> reviewImageOptional = reviewImageRepository.findById(id);
        return reviewImageOptional.map(reviewImageMapper::toDto);
    }


    public Page<ReviewImage> getPage(Integer page, Integer size) {
        if (page == null || size == null) {
            var reviewImages = findAll();
            if (reviewImages.isEmpty()) {
                return Page.empty();
            }
            return new PageImpl<>(reviewImages);
        }
        if (page < 0 || size < 1) {
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        return reviewImageRepository.findAll(pageRequest);
    }

    public Page<ReviewImageDto> getPageDto(Integer page, Integer size) {

        if (page == null || size == null) {
            var reviewImages = findAllDto();
            if (reviewImages.isEmpty()) {
                return Page.empty();
            }
            return new PageImpl<>(reviewImages);
        }
        if (page < 0 || size < 1) {
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ReviewImage> reviewImagePage = reviewImageRepository.findAll(pageRequest);
        return reviewImagePage.map(reviewImageMapper::toDto);
    }

    @Transactional
    public ReviewImage save(ReviewImage reviewImage) {
        return reviewImageRepository.save(reviewImage);
    }

    @Transactional
    public ReviewImageDto saveDto(ReviewImageDto reviewImageDto) {
        reviewImageDto.setId(null);
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
    public Optional<ReviewImageDto> updateDto(Long id, ReviewImageUpdateDto reviewImageUpdateDto) {
        Optional<ReviewImage> imageOptional = findById(id);
        if (imageOptional.isEmpty()) {
            return Optional.empty();
        }

        ReviewImage currentReviewImage = imageOptional.get();

        if (reviewImageUpdateDto.getName() != null) {
            currentReviewImage.setName(reviewImageUpdateDto.getName());
        }
        if (reviewImageUpdateDto.getData() != null) {
            currentReviewImage.setData(reviewImageUpdateDto.getData());
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
        List<ReviewImage> imageList = reviewImageMapper.toEntityList(imageDtoList);
        return reviewImageRepository.saveAll(imageList);
    }
}