package com.gitlab.controller;

import com.gitlab.controllers.api.rest.ProductCategoryRestApi;
import com.gitlab.dto.ProductCategoryDto;
import com.gitlab.model.ProductCategory;
import com.gitlab.service.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class ProductCategoryRestController implements ProductCategoryRestApi {

    private final ProductCategoryService productCategoryService;

    public ResponseEntity<List<ProductCategoryDto>> getPage(Integer page, Integer size) {
        log.info("Request to get page of product categories: page = {}, size = {}", page, size);
        var productCategoryPage = productCategoryService.getPageDto(page, size);
        if (productCategoryPage == null || productCategoryPage.getContent().isEmpty()) {
            log.info("No product categories found.");
            return ResponseEntity.noContent().build();
        }
        log.info("Returning product categories.");
        return ResponseEntity.ok(productCategoryPage.getContent());
    }

    @Override
    public ResponseEntity<ProductCategoryDto> get(Long id) {
        log.info("Request to get product category by id: {}", id);
        return productCategoryService.findByIdDto(id)
                .map(dto -> {
                    log.info("Product category found: {}", dto);
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> {
                    log.info("Product category with id {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Override
    public ResponseEntity<ProductCategoryDto> create(ProductCategoryDto productCategoryDto) {
        log.info("Request to create product category: {}", productCategoryDto);
        ProductCategoryDto savedProductCategoryDto = productCategoryService.saveDto(productCategoryDto);
        log.info("Product category created: {}", savedProductCategoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProductCategoryDto);
    }

    @Override
    public ResponseEntity<ProductCategoryDto> update(Long id, ProductCategoryDto productCategoryDto) {
        log.info("Request to update product category with id: {}", id);
        Optional<ProductCategoryDto> updatedProductCategoryDto = productCategoryService.updateDto(id, productCategoryDto);
        return updatedProductCategoryDto
                .map(dto -> {
                    log.info("Product category updated: {}", dto);
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> {
                    log.info("Product category with id {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        log.info("Request to delete product category with id: {}", id);
        Optional<ProductCategory> product = productCategoryService.delete(id);
        if (product.isEmpty()) {
            log.info("Product category with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Product category with id {} deleted", id);
        return ResponseEntity.ok().build();
    }
}
