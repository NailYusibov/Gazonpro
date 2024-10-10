package com.gitlab.controller;

import com.gitlab.controllers.api.rest.ProductImageRestApi;
import com.gitlab.dto.ProductImageDto;
import com.gitlab.model.ProductImage;
import com.gitlab.service.ProductImageService;
import com.gitlab.util.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ProductImageController implements ProductImageRestApi {

    private final ProductImageService productImageService;
    private static final Logger log = LoggerFactory.getLogger(ProductImageController.class);

    public ResponseEntity<List<ProductImageDto>> getPage(Integer page, Integer size) {
        log.info("Request to get product images page: page={}, size={}", page, size);
        var productImagePage = productImageService.getPageDto(page, size);
        if (productImagePage == null || productImagePage.getContent().isEmpty()) {
            log.info("No content found for page={}, size={}", page, size);
            return ResponseEntity.noContent().build();
        }
        log.info("Returning {} product images for page={}, size={}", productImagePage.getContent().size(), page, size);
        return ResponseEntity.ok(productImagePage.getContent());
    }

    @Override
    public ResponseEntity<?> get(@PathVariable Long id) {
        log.info("Request to get product image by id: {}", id);
        Optional<ProductImageDto> productImage = productImageService.findByIdDto(id);
        if (productImage.isEmpty()) {
            log.warn("Product image with id {} not found", id);
            return ResponseEntity.notFound().build();
        }

        if (productImage.get().getData().length < 60) {
            log.warn("Product image with id {} has partial content", id);
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(productImage.get());
        }

        log.info("Returning product image with id {}", id);
        return productImage.map(image ->
                ResponseEntity.status(HttpStatus.OK)
                        .contentType(MediaType.parseMediaType(MediaType.IMAGE_JPEG_VALUE))
                        .body(ImageUtils.decompressImage(image.getData()))).orElse(null);
    }

    @Override
    public ResponseEntity<List<ProductImageDto>> getAllByProductId(Long id) {
        log.info("Request to get all product images by product id: {}", id);
        List<ProductImageDto> productImageDtos = productImageService.findAllByProductIdDto(id);
        if (productImageDtos.isEmpty()) {
            log.info("No product images found for product id: {}", id);
            return ResponseEntity.noContent().build();
        }
        log.info("Returning {} product images for product id: {}", productImageDtos.size(), id);
        return ResponseEntity.ok(productImageDtos);
    }

    @Override
    public ResponseEntity<ProductImageDto> create(ProductImageDto productImageDto) {
        log.info("Request to create new product image: {}", productImageDto);
        ProductImageDto savedProductImageDto = productImageService.saveDto(productImageDto);
        log.info("Product image created with id: {}", savedProductImageDto.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedProductImageDto);
    }

    @Override
    public ResponseEntity<ProductImageDto> update(Long id, ProductImageDto productImageDto) {
        log.info("Request to update product image with id: {}", id);
        Optional<ProductImageDto> updatedProductImageDto = productImageService.updateDto(id, productImageDto);
        if (updatedProductImageDto.isPresent()) {
            log.info("Product image with id {} updated successfully", id);
            return ResponseEntity.ok(updatedProductImageDto.get());
        } else {
            log.warn("Product image with id {} not found for update", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Request to delete product image with id: {}", id);
        Optional<ProductImage> productImage = productImageService.delete(id);
        if (productImage.isEmpty()) {
            log.warn("Product image with id {} not found for deletion", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Product image with id {} deleted successfully", id);
        return ResponseEntity.ok().build();
    }
}
