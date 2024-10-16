package com.gitlab.mapper;

import com.gitlab.dto.ProductDto;
import com.gitlab.model.*;
import com.gitlab.service.ProductImageService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class ProductMapper {

    @Autowired
    private ProductImageService productImageService;

    @Mapping(source = "productImages", target = "imagesId")
    @Mapping(source = "review", target = "rating")
    @Mapping(source = "store", target = "storeId")
    public abstract ProductDto toDto(Product product);

    public Long map(Store store) {
        if (store == null) {
            return null;
        } else {
            return store.getId();
        }
    }

    public String map(ProductCategory productCategory) {
        if (productCategory == null) {
            return null;
        }
        return productCategory.getName();
    }

    public ProductCategory map(String productCategory) {
        if (productCategory == null) {
            return null;
        }
        ProductCategory category = new ProductCategory();
        category.setName(productCategory);
        return category;
    }


    public Long[] mapProductImagesToImagesId(Set<ProductImage> productImages) {
        if (productImages == null || productImages.isEmpty()) {
            return new Long[0];
        }
        return productImages.stream()
                .map(ProductImage::getId)
                .toArray(Long[]::new);
    }

    public String mapProductReviewsToRating(Set<Review> productReviews) {
        if (productReviews == null || productReviews.isEmpty()) {
            return "Нет оценок";
        }

        return String.format("%.2f", productReviews.stream()
                .map(Review::getRating).mapToInt(a -> a).average().orElse(0));
    }

    @Mapping(source = "imagesId", target = "productImages")
    public abstract Product toEntity(ProductDto exampleDto);

    public Set<ProductImage> mapImagesIdToProductImages(Long[] imagesId) {
        if (imagesId == null || imagesId.length == 0) {
            return null;
        }
        return Arrays.stream(imagesId)
                .map(productImageService::findById).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
    }

    public abstract List<ProductDto> toDtoList(List<Product> productList);

    public abstract List<Product> toEntityList(List<ProductDto> productDtoList);
}