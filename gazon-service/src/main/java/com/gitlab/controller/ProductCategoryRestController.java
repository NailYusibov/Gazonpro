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
        var productCategoryPage = productCategoryService.getPageDto(page, size);
        if (productCategoryPage == null || productCategoryPage.getContent().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productCategoryPage.getContent());
    }

    @Override
    public ResponseEntity<ProductCategoryDto> get(Long id) {
        return productCategoryService.findByIdDto(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<ProductCategoryDto> create(ProductCategoryDto productCategoryDto) {
        ProductCategoryDto savedProductCategoryDto = productCategoryService.saveDto(productCategoryDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedProductCategoryDto);
    }

    @Override
    public ResponseEntity<ProductCategoryDto> update(Long id, ProductCategoryDto productCategoryDto) {
        Optional<ProductCategoryDto> updatedProductCategoryDto = productCategoryService.updateDto(id, productCategoryDto);

        return updatedProductCategoryDto
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        Optional<ProductCategory> product = productCategoryService.delete(id);
        return product.isEmpty() ?
                ResponseEntity.notFound().build() :
                ResponseEntity.ok().build();
    }
}

