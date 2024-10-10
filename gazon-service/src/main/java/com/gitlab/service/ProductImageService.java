package com.gitlab.service;

import com.gitlab.dto.ProductImageDto;
import com.gitlab.mapper.ProductImageMapper;
import com.gitlab.model.ProductImage;
import com.gitlab.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
//        (readOnly = true)
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductImageMapper productImageMapper;

    public List<ProductImage> findAll() {
        log.info("Fetching all product images");
        return productImageRepository.findAll();
    }

    public List<ProductImageDto> findAllDto() {
        log.info("Fetching all product image DTOs");
        List<ProductImage> productImages = productImageRepository.findAll();
        List<ProductImageDto> productImageDtos = productImages.stream()
                .map(productImageMapper::toDto)
                .collect(Collectors.toList());
        log.info("Found product image DTOs: {}", productImageDtos);
        return productImageDtos;
    }

    public Optional<ProductImage> findById(Long id) {
        log.info("Fetching product image by id: {}", id);
        return productImageRepository.findById(id);
    }

    public Optional<ProductImageDto> findByIdDto(Long id) {
        log.info("Fetching product image DTO by id: {}", id);
        Optional<ProductImageDto> productImageDto = productImageRepository.findById(id)
                .map(productImageMapper::toDto);
        productImageDto.ifPresent(dto -> log.info("Found product image DTO: {}", dto));
        return productImageDto;
    }

    public List<ProductImage> findAllByProductId(Long id) {
        log.info("Fetching all product images for product id: {}", id);
        return productImageRepository.findAllBySomeProductId(id);
    }

    public List<ProductImageDto> findAllByProductIdDto(Long id) {
        log.info("Fetching all product image DTOs for product id: {}", id);
        List<ProductImageDto> productImageDtos = findAllByProductId(id)
                .stream()
                .map(productImageMapper::toDto)
                .collect(Collectors.toList());
        log.info("Found product image DTOs for product id {}: {}", id, productImageDtos);
        return productImageDtos;
    }

    public Page<ProductImage> getPage(Integer page, Integer size) {
        log.info("Fetching product image page: page = {}, size = {}", page, size);
        if (page == null || size == null) {
            var productImages = findAll();
            if (productImages.isEmpty()) {
                log.info("No product images found.");
                return Page.empty();
            }
            return new PageImpl<>(productImages);
        }
        if (page < 0 || size < 1) {
            log.warn("Invalid page or size parameters");
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        return productImageRepository.findAll(pageRequest);
    }

    public Page<ProductImageDto> getPageDto(Integer page, Integer size) {
        log.info("Fetching product image DTO page: page = {}, size = {}", page, size);
        if (page == null || size == null) {
            var productImages = findAllDto();
            if (productImages.isEmpty()) {
                log.info("No product image DTOs found.");
                return Page.empty();
            }
            return new PageImpl<>(productImages);
        }
        if (page < 0 || size < 1) {
            log.warn("Invalid page or size parameters");
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ProductImage> productImagePage = productImageRepository.findAll(pageRequest);
        return productImagePage.map(productImageMapper::toDto);
    }

    @Transactional
    public ProductImage save(ProductImage productImage) {
        log.info("Saving product image: {}", productImage);
        return productImageRepository.save(productImage);
    }

    public ProductImageDto saveDto(ProductImageDto productImageDto) {
        log.info("Saving product image DTO: {}", productImageDto);
        if (productImageDto == null || (productImageDto.getProductId() == null && productImageDto.getName() == null && productImageDto.getData() == null)) {
            log.error("Invalid productImageDto: productImageDto cannot be null or have all fields null");
            throw new IllegalArgumentException("productImageDto cannot be null or have all fields null");
        }
        ProductImage productImage = productImageMapper.toEntity(productImageDto);
        ProductImage savedProductImage = productImageRepository.save(productImage);
        ProductImageDto savedProductImageDto = productImageMapper.toDto(savedProductImage);
        log.info("Product image DTO saved: {}", savedProductImageDto);
        return savedProductImageDto;
    }

    @Transactional
    public Optional<ProductImage> update(Long id, ProductImage productImage) {
        log.info("Updating product image with id {}: {}", id, productImage);
        Optional<ProductImage> currentOptionalImage = findById(id);
        ProductImage currentImage;
        if (currentOptionalImage.isEmpty()) {
            log.warn("Product image with id {} not found", id);
            return currentOptionalImage;
        } else {
            currentImage = currentOptionalImage.get();
        }
        if (productImage.getName() != null) {
            currentImage.setName(productImage.getName());
        }
        if (productImage.getData() != null) {
            currentImage.setData(productImage.getData());
        }
        ProductImage updatedImage = productImageRepository.save(currentImage);
        log.info("Product image with id {} updated: {}", id, updatedImage);
        return Optional.of(updatedImage);
    }

    @Transactional
    public Optional<ProductImageDto> updateDto(Long id, ProductImageDto productImageDto) {
        log.info("Updating product image DTO with id {}: {}", id, productImageDto);
        Optional<ProductImage> currentOptionalImage = findById(id);

        if (currentOptionalImage.isEmpty()) {
            log.warn("Product image DTO with id {} not found", id);
            return Optional.empty();
        }

        ProductImage currentImage = currentOptionalImage.get();

        if (productImageDto.getName() != null) {
            currentImage.setName(productImageDto.getName());
        }

        if (productImageDto.getData() != null) {
            currentImage.setData(productImageDto.getData());
        }

        ProductImage updatedImage = productImageRepository.save(currentImage);
        ProductImageDto updatedProductImageDto = productImageMapper.toDto(updatedImage);
        log.info("Product image DTO with id {} updated: {}", id, updatedProductImageDto);
        return Optional.of(updatedProductImageDto);
    }

    @Transactional
    public Optional<ProductImage> delete(Long id) {
        log.info("Deleting product image with id: {}", id);
        Optional<ProductImage> foundProductImage = findById(id);
        if (foundProductImage.isPresent()) {
            productImageRepository.deleteById(id);
            log.info("Product image with id {} deleted", id);
        } else {
            log.warn("Product image with id {} not found", id);
        }
        return foundProductImage;
    }

    @Transactional
    public Optional<ProductImageDto> deleteDto(Long id) {
        log.info("Deleting product image DTO with id: {}", id);
        Optional<ProductImage> foundProductImage = findById(id);
        if (foundProductImage.isPresent()) {
            productImageRepository.deleteById(id);
            log.info("Product image DTO with id {} deleted", id);
            return foundProductImage.map(productImageMapper::toDto);
        } else {
            log.warn("Product image DTO with id {} not found", id);
            return Optional.empty();
        }
    }

    @Transactional
    public List<ProductImage> saveAll(List<ProductImage> imageList) {
        log.info("Saving all product images: {}", imageList);
        List<ProductImage> savedImageList = productImageRepository.saveAll(imageList);
        log.info("Saved all product images: {}", savedImageList);
        return savedImageList;
    }

    public List<ProductImageDto> saveAllDto(List<ProductImageDto> imageDtoList) {
        log.info("Saving all product image DTOs: {}", imageDtoList);
        List<ProductImage> imageList = imageDtoList.stream()
                .map(productImageMapper::toEntity)
                .collect(Collectors.toList());

        List<ProductImage> savedImageList = productImageRepository.saveAll(imageList);
        List<ProductImageDto> savedImageDtoList = savedImageList.stream()
                .map(productImageMapper::toDto)
                .collect(Collectors.toList());
        log.info("Saved all product image DTOs: {}", savedImageDtoList);
        return savedImageDtoList;
    }
}
