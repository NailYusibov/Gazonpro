package com.gitlab.service;

import com.gitlab.model.ProductImage;
import com.gitlab.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository productImageRepository;

    public List<ProductImage> findAll() {
        return productImageRepository.findAll();
    }

    public Optional<ProductImage> findById(Long id) {
        return productImageRepository.findById(id);
    }
}