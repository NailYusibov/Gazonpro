package com.gitlab.mapper;

import com.gitlab.dto.ReviewDto;
import com.gitlab.model.Product;
import com.gitlab.model.Review;
import com.gitlab.model.ReviewImage;
import com.gitlab.model.User;
import com.gitlab.repository.UserRepository;
import com.gitlab.service.ProductService;
import com.gitlab.service.ReviewImageService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class ReviewMapper {

    @Autowired
    protected ReviewImageService reviewImageService;
    @Autowired
    protected ProductService productService;
    @Autowired
    protected UserRepository userRepository;

    @Mapping(source = "reviewImages", target = "reviewImagesId")
    @Mapping(source = "product", target = "productId")
    @Mapping(source = "user", target = "userId")
    public abstract ReviewDto toDto(Review review);

    public Long[] mapReviewImagesToReviewImagesId(Set<ReviewImage> reviewImages) {
        if (reviewImages == null || reviewImages.isEmpty()) {
            return null;
        }
        return reviewImages.stream()
                .map(ReviewImage::getId)
                .toArray(Long[]::new);
    }

    public Long mapProductToProductId(Product product) {
        if (product == null) {
            return null;
        }
        return product.getId();
    }

    public Long mapUserToUserId(User user) {
        if (user == null) {
            return null;
        }
        return user.getId();
    }

    @Mapping(source = "reviewImagesId", target = "reviewImages")
    @Mapping(source = "productId", target = "product")
    @Mapping(source = "userId", target = "user")
    public abstract Review toEntity(ReviewDto reviewDto);

    public Set<ReviewImage> mapReviewImagesIdToReviewImages(Long[] imagesId) {
        if (imagesId == null || imagesId.length == 0) {
            return null;
        }
        return Arrays.stream(imagesId)
                .map(reviewImageService::findById).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
    }

    public Product mapProductIdToProduct(Long productId) {
        if (productId == null) {
            return null;
        }
        return productService.findById(productId).
                orElseThrow(() -> new RuntimeException("Product wasn't found"));
    }

    public User mapUserIdToUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId).
                orElseThrow(() -> new RuntimeException("User wasn't found"));
    }

    public abstract List<ReviewDto> toDtoList(List<Review> reviewList);

    public abstract List<Review> toEntityList(List<ReviewDto> reviewDtoList);
}