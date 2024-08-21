package com.gitlab.service;

import com.gitlab.model.ProductImage;
import com.gitlab.repository.ProductImageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductImageServiceTest {

    @Mock
    private ProductImageRepository productImageRepository;
    @InjectMocks
    private ProductImageService productImageService;

    @Test
    void should_find_all_productImages() {
        List<ProductImage> expectedResult = generateProductImages();
        when(productImageRepository.findAll()).thenReturn(generateProductImages());

        List<ProductImage> actualResult = productImageService.findAll();

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void should_find_productImage_by_id() {
        long id = 1L;
        ProductImage expectedResult = generateProductImage();
        when(productImageRepository.findById(id)).thenReturn(Optional.of(expectedResult));

        Optional<ProductImage> actualResult = productImageService.findById(id);

        assertEquals(expectedResult, actualResult.orElse(null));
    }

    private List<ProductImage> generateProductImages() {
        return List.of(
                new ProductImage(1L, null, "name1", new byte[1]),
                new ProductImage(1L, null, "name1", new byte[1]),
                new ProductImage(1L, null, "name1", new byte[1]),
                new ProductImage(1L, null, "name1", new byte[1]));
    }

    private ProductImage generateProductImage() {
        return new ProductImage(1L, null, "name1", new byte[1]);
    }
}
