package com.gitlab.service;

import com.gitlab.dto.ProductCategoryDto;
import com.gitlab.mapper.ProductCategoryMapper;
import com.gitlab.model.ProductCategory;
import com.gitlab.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;
    private final ProductCategoryMapper productCategoryMapper;

    public List<ProductCategory> findAll() {
        log.info("Finding all product categories");
        List<ProductCategory> productCategories = productCategoryRepository.findAll();
        log.info("Found product categories: {}", productCategories);
        return productCategories;
    }

    public List<ProductCategoryDto> findAllDto() {
        log.info("Finding all product categories as DTOs");
        List<ProductCategoryDto> productCategoryDtos = productCategoryMapper.toDtoList(productCategoryRepository.findAll());
        log.info("Found product category DTOs: {}", productCategoryDtos);
        return productCategoryDtos;
    }

    public Optional<ProductCategoryDto> findByIdDto(Long id) {
        log.info("Finding product category by id: {}", id);
        Optional<ProductCategory> productCategoryOptional = productCategoryRepository.findById(id);
        productCategoryOptional.ifPresentOrElse(
                category -> log.info("Found product category: {}", category),
                () -> log.warn("Product category with id {} not found", id)
        );
        return productCategoryOptional.map(productCategoryMapper::toDto);
    }

    public Page<ProductCategoryDto> getPageDto(Integer page, Integer size) {
        log.info("Getting product category page: page = {}, size = {}", page, size);

        if (page == null || size == null) {
            var productCategory = findAllDto();
            log.info("Returning all product categories as no pagination parameters were provided");
            return productCategory.isEmpty() ? Page.empty() : new PageImpl<>(productCategory);
        }

        if (page < 0 || size < 1) {
            log.warn("Invalid pagination parameters: page = {}, size = {}", page, size);
            return Page.empty();
        }

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ProductCategory> productCategoryPage = productCategoryRepository.findAll(pageRequest);
        log.info("Returning product category page with {} items", productCategoryPage.getContent().size());

        return productCategoryPage.map(productCategoryMapper::toDto);
    }

    public ProductCategoryDto saveDto(ProductCategoryDto productCategoryDto) {
        log.info("Saving product category DTO: {}", productCategoryDto);
        ProductCategory savedProductCategory = productCategoryRepository.save(productCategoryMapper.toEntity(productCategoryDto));
        ProductCategoryDto savedDto = productCategoryMapper.toDto(savedProductCategory);
        log.info("Saved product category DTO: {}", savedDto);
        return savedDto;
    }

    public Optional<ProductCategoryDto> updateDto(Long id, ProductCategoryDto productCategoryDto) {
        log.info("Updating product category with id: {}", id);
        Optional<ProductCategory> optionalSavedProductCategory = productCategoryRepository.findById(id);

        if (optionalSavedProductCategory.isEmpty()) {
            log.warn("Product category with id {} not found", id);
            return Optional.empty();
        }

        ProductCategory savedProductCategory = optionalSavedProductCategory.get();

        if (productCategoryDto.getName() != null) {
            log.info("Updating product category name to: {}", productCategoryDto.getName());
            savedProductCategory.setName(productCategoryDto.getName());
        }

        ProductCategory updatedProductCategory = productCategoryRepository.save(savedProductCategory);
        ProductCategoryDto updatedDto = productCategoryMapper.toDto(updatedProductCategory);
        log.info("Updated product category DTO: {}", updatedDto);
        return Optional.ofNullable(updatedDto);
    }

    public Optional<ProductCategory> delete(Long id) {
        log.info("Deleting product category with id: {}", id);
        Optional<ProductCategory> foundProductCategory = productCategoryRepository.findById(id);
        foundProductCategory.ifPresentOrElse(
                category -> {
                    productCategoryRepository.deleteById(id);
                    log.info("Deleted product category: {}", category);
                },
                () -> log.warn("Product category with id {} not found", id)
        );
        return foundProductCategory;
    }
}
