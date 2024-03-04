package com.gitlab.service;

import com.gitlab.dto.ProductCategoryDto;
import com.gitlab.mapper.ProductCategoryMapper;
import com.gitlab.model.ProductCategory;
import com.gitlab.repository.ProductCategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductCategoryServiceTest {

    @Mock
    private ProductCategoryRepository productCategoryRepository;
    @InjectMocks
    private ProductCategoryService productCategoryService;
    @Mock
    private ProductCategoryMapper productCategoryMapper;

    @Test
    void should_find_all_productCategory() {
        List<ProductCategory> expectedResult = generateProductCategoryList();
        when(productCategoryRepository.findAll()).thenReturn(generateProductCategoryList());

        List<ProductCategory> actualResult = productCategoryService.findAll();

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void should_find_productCategoryDto_by_id_when_exists() {

        Long id = 1L;
        ProductCategoryDto expectedDto = new ProductCategoryDto();
        expectedDto.setId(id);
        expectedDto.setName("CategoryName");

        ProductCategory productCategory = new ProductCategory();
        productCategory.setId(id);
        productCategory.setName("CategoryName");

        Optional<ProductCategory> productCategoryOptional = Optional.of(productCategory);

        when(productCategoryRepository.findById(id)).thenReturn(productCategoryOptional);
        when(productCategoryMapper.toDto(productCategory)).thenReturn(expectedDto);


        Optional<ProductCategoryDto> result = productCategoryService.findByIdDto(id);

        assertTrue(result.isPresent());
        assertEquals(expectedDto.getId(), result.get().getId());
        assertEquals(expectedDto.getName(), result.get().getName());
    }

    @Test
    void should_save_productCategoryDto() {

        ProductCategoryDto inputDto = new ProductCategoryDto(null, "CategoryName");
        ProductCategory savedProductCategory = new ProductCategory(1L, "CategoryName");
        ProductCategoryDto expectedDto = new ProductCategoryDto(1L, "CategoryName");

        when(productCategoryMapper.toEntity(inputDto)).thenReturn(savedProductCategory);
        when(productCategoryRepository.save(savedProductCategory)).thenReturn(savedProductCategory);
        when(productCategoryMapper.toDto(savedProductCategory)).thenReturn(expectedDto);


        ProductCategoryDto result = productCategoryService.saveDto(inputDto);


        assertEquals(expectedDto, result);
        verify(productCategoryMapper).toEntity(inputDto);
        verify(productCategoryRepository).save(savedProductCategory);
        verify(productCategoryMapper).toDto(savedProductCategory);
    }

    @Test
    void should_update_productCategoryDto_when_id_exists() {

        Long id = 1L;
        ProductCategoryDto productCategoryDto = new ProductCategoryDto();
        productCategoryDto.setName("UpdatedName");

        ProductCategory savedProductCategory = new ProductCategory();
        savedProductCategory.setId(id);
        savedProductCategory.setName("OriginalName");

        Optional<ProductCategory> optionalSavedProductCategory = Optional.of(savedProductCategory);
        when(productCategoryRepository.findById(id)).thenReturn(optionalSavedProductCategory);

        ProductCategory updatedProductCategory = new ProductCategory();
        updatedProductCategory.setId(id);
        updatedProductCategory.setName("UpdatedName");
        when(productCategoryRepository.save(savedProductCategory)).thenReturn(updatedProductCategory);

        ProductCategoryDto expectedDto = new ProductCategoryDto();
        expectedDto.setId(id);
        expectedDto.setName("UpdatedName");
        when(productCategoryMapper.toDto(updatedProductCategory)).thenReturn(expectedDto);


        Optional<ProductCategoryDto> result = productCategoryService.updateDto(id, productCategoryDto);


        assertTrue(result.isPresent());
        assertEquals("UpdatedName", result.get().getName());
    }
    @Test
    void should_delete_productCategory_by_id_when_exists() {

        Long id = 1L;
        ProductCategory productCategory = new ProductCategory();
        productCategory.setId(id);
        Optional<ProductCategory> foundProductCategory = Optional.of(productCategory);

        when(productCategoryRepository.findById(id)).thenReturn(foundProductCategory);

        Optional<ProductCategory> result = productCategoryService.delete(id);

        assertTrue(result.isPresent());
        verify(productCategoryRepository).deleteById(id);
    }


    private List<ProductCategory> generateProductCategoryList() {
        return List.of(
                new ProductCategory(1L, "Name1"),
                new ProductCategory(2L, "Name2"),
                new ProductCategory(3L, "Name3"),
                new ProductCategory(4L, "Name4"));
    }



}
