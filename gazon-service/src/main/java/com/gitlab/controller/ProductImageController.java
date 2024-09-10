package com.gitlab.controller;

import com.gitlab.controllers.api.rest.ProductImageRestApi;
import com.gitlab.dto.ProductImageDto;
import com.gitlab.model.ProductImage;
import com.gitlab.service.ProductImageService;
import com.gitlab.util.ImageUtils;
import lombok.RequiredArgsConstructor;
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

    public ResponseEntity<List<ProductImageDto>> getPage(Integer page, Integer size) {
        var productImagePage = productImageService.getPageDto(page, size);
        if (productImagePage == null || productImagePage.getContent().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productImagePage.getContent());
    }

    @Override
    public ResponseEntity<?> get(@PathVariable Long id) {
        Optional<ProductImageDto> productImage = productImageService.findByIdDto(id);
        if (productImage.isEmpty())
            return ResponseEntity.notFound().build();

        if (productImage.get().getData().length < 60)
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(productImage.get());

        return productImage.map(image ->
                ResponseEntity.status(HttpStatus.OK)
                        .contentType(MediaType.parseMediaType(MediaType.IMAGE_JPEG_VALUE))
                        .body(ImageUtils.decompressImage(image.getData()))).orElse(null);
    }

    @Override
    public ResponseEntity<List<ProductImageDto>> getAllByProductId(Long id) {
        List<ProductImageDto> productImageDtos = productImageService.findAllByProductIdDto(id);
        return productImageDtos.isEmpty()
                ? ResponseEntity.noContent().build() :
                ResponseEntity.ok(productImageDtos);
    }

    @Override
    public ResponseEntity<ProductImageDto> create(ProductImageDto productImageDto) {
        ProductImageDto saveProductImageDto = productImageService.saveDto(productImageDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saveProductImageDto);
    }

    @Override
    public ResponseEntity<ProductImageDto> update(Long id, ProductImageDto productImageDto) {
        Optional<ProductImageDto> updatedProductImageDto = productImageService.updateDto(id, productImageDto);
        return updatedProductImageDto
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Optional<ProductImage> productImage = productImageService.delete(id);

        return productImage.isEmpty()
                ? ResponseEntity.notFound().build() :
                ResponseEntity.ok().build();
    }
}