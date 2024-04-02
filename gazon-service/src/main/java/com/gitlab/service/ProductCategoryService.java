package com.gitlab.service;

import com.gitlab.dto.ProductCategoryDto;
import com.gitlab.mapper.ProductCategoryMapper;
import com.gitlab.model.ProductCategory;
import com.gitlab.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    private final ProductCategoryMapper productCategoryMapper;

    public List<ProductCategory> findAll() {
        return productCategoryRepository.findAll();
    }

    public List<ProductCategoryDto> findAllDto() {
        return productCategoryMapper.toDtoList(productCategoryRepository.findAll());
    }

    public Optional<ProductCategoryDto> findByIdDto(Long id) {
        Optional<ProductCategory> productCategoryOptional = productCategoryRepository.findById(id);
        if (productCategoryOptional.isPresent()) {
            return productCategoryOptional.map(productCategoryMapper::toDto);
        }
        return Optional.empty();
    }


    public Page<ProductCategoryDto> getPageDto(Integer page, Integer size) {

        if (page == null || size == null) {
            var productCategory = findAllDto();
            if (productCategory.isEmpty()) {
                return Page.empty();
            }
            return new PageImpl<>(productCategory);
        }
        if (page < 0 || size < 1) {
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ProductCategory> productCategoryPage = productCategoryRepository.findAll(pageRequest);
        return productCategoryPage.map(productCategoryMapper::toDto);
    }


    public ProductCategoryDto saveDto(ProductCategoryDto productCategoryDto) {
        return productCategoryMapper.toDto(productCategoryRepository
                .save(productCategoryMapper.toEntity(productCategoryDto)));
    }


    public Optional<ProductCategoryDto> updateDto(Long id, ProductCategoryDto productCategoryDto) {
        Optional<ProductCategory> optionalSavedProductCategory = productCategoryRepository.findById(id);
        if (optionalSavedProductCategory.isEmpty()) {
            return Optional.empty();
        }

        ProductCategory savedProductCategory = optionalSavedProductCategory.get();

        if (productCategoryDto.getName() != null) {
            savedProductCategory.setName(productCategoryDto.getName());
        }

        ProductCategory updatedProductCategory = productCategoryRepository.save(savedProductCategory);
        return Optional.ofNullable(productCategoryMapper.toDto(updatedProductCategory));
    }

    public Optional<ProductCategory> delete(Long id) {
        Optional<ProductCategory> foundProductCategory = productCategoryRepository.findById(id);
        if (foundProductCategory.isPresent()) {
            productCategoryRepository.deleteById(id);
        }
        return foundProductCategory;
    }

}