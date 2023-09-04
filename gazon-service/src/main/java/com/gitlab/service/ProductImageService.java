package com.gitlab.service;

import com.gitlab.dto.ProductImageDto;
import com.gitlab.mapper.ProductImageMapper;
import com.gitlab.model.ProductImage;
import com.gitlab.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductImageMapper productImageMapper;

    public List<ProductImage> findAll() {
        return productImageRepository.findAll();
    }

    public List<ProductImageDto> findAllDto() {
        List<ProductImage> productImages = productImageRepository.findAll();
        return productImages.stream()
                .map(productImageMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<ProductImage> findById(Long id) {
        return productImageRepository.findById(id);
    }

    public Optional<ProductImageDto> findByIdDto(Long id) {
        return productImageRepository.findById(id)
                .map(productImageMapper::toDto);
    }

    @Transactional
    public ProductImage save(ProductImage productImage) {
        return productImageRepository.save(productImage);
    }

    public ProductImageDto saveDto(ProductImageDto productImageDto) {
        ProductImage productImage = productImageMapper.toEntity(productImageDto);
        ProductImage savedProductImage = productImageRepository.save(productImage);
        return productImageMapper.toDto(savedProductImage);
    }

    @Transactional
    public Optional<ProductImage> update(Long id, ProductImage productImage) {
        Optional<ProductImage> currentOptionalImage = findById(id);
        ProductImage currentImage;
        if (currentOptionalImage.isEmpty()) {
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
        return Optional.of(productImageRepository.save(currentImage));
    }

    @Transactional
    public Optional<ProductImageDto> updateDto(Long id, ProductImageDto productImageDto) {
        Optional<ProductImage> currentOptionalImage = findById(id);

        if (currentOptionalImage.isEmpty()) {
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
        return Optional.ofNullable(productImageMapper.toDto(updatedImage));
    }

    @Transactional
    public Optional<ProductImage> delete(Long id) {
        Optional<ProductImage> foundProductImage = findById(id);
        if (foundProductImage.isPresent()) {
            productImageRepository.deleteById(id);
        }
        return foundProductImage;
    }

    @Transactional
    public Optional<ProductImageDto> deleteDto(Long id) {
        Optional<ProductImage> foundProductImage = findById(id);
        if (foundProductImage.isPresent()) {
            productImageRepository.deleteById(id);
            return foundProductImage.map(productImageMapper::toDto);
        }
        return Optional.empty();
    }

    @Transactional
    public List<ProductImage> saveAll(List<ProductImage> imageList) {
        return productImageRepository.saveAll(imageList);
    }

    public List<ProductImageDto> saveAllDto(List<ProductImageDto> imageDtoList) {
        List<ProductImage> imageList = imageDtoList.stream()
                .map(productImageMapper::toEntity)
                .collect(Collectors.toList());

        List<ProductImage> savedImageList = productImageRepository.saveAll(imageList);

        return savedImageList.stream()
                .map(productImageMapper::toDto)
                .collect(Collectors.toList());
    }
}