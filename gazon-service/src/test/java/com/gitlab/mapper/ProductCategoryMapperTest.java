package com.gitlab.mapper;

import com.gitlab.dto.ProductCategoryDto;
import com.gitlab.model.ProductCategory;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProductCategoryMapperTest {

    private final ProductCategoryMapper mapper = Mappers.getMapper(ProductCategoryMapper.class);

    @Test
    void should_map_productCategory_to_Dto() {
        ProductCategory productCategory = getProductCategory(1L);

        ProductCategoryDto actualResult = mapper.toDto(productCategory);

        assertNotNull(actualResult);
        assertEquals(productCategory.getId(), actualResult.getId());
        assertEquals(productCategory.getName(), actualResult.getName());

    }

    @Test
    void should_map_productCategoryDto_to_Entity() {
        ProductCategoryDto productCategoryDto = new ProductCategoryDto();
        productCategoryDto.setId(1L);
        productCategoryDto.setName("Name");


        ProductCategory actualResult = mapper.toEntity(productCategoryDto);

        assertNotNull(actualResult);
        assertEquals(productCategoryDto.getId(), actualResult.getId());
        assertEquals(productCategoryDto.getName(), actualResult.getName());

    }

    @Test
    void should_map_productCategoryList_to_DtoList() {
        List<ProductCategory> productCategoryList = List.of(getProductCategory(1L), getProductCategory(2L), getProductCategory(3L));

        List<ProductCategoryDto> productCategoryDtoList = mapper.toDtoList(productCategoryList);

        assertNotNull(productCategoryDtoList);
        assertEquals(productCategoryList.size(), productCategoryDtoList.size());
        for (int i = 0; i < productCategoryDtoList.size(); i++) {
            ProductCategoryDto dto = productCategoryDtoList.get(i);
            ProductCategory entity = productCategoryList.get(i);
            assertEquals(dto.getId(), entity.getId());
            assertEquals(dto.getName(), entity.getName());
        }
    }

    @Test
    void should_map_productCategoryDtoList_to_EntityList() {
        List<ProductCategoryDto> productCategoryDtoList = List.of(getProductCategoryDto(1L), getProductCategoryDto(2L), getProductCategoryDto(3L));

        List<ProductCategory> productCategoryList = mapper.toEntityList(productCategoryDtoList);

        assertNotNull(productCategoryList);
        assertEquals(productCategoryList.size(), productCategoryDtoList.size());
        for (int i = 0; i < productCategoryList.size(); i++) {
            ProductCategoryDto dto = productCategoryDtoList.get(i);
            ProductCategory entity = productCategoryList.get(i);
            assertEquals(dto.getId(), entity.getId());
            assertEquals(dto.getName(), entity.getName());
        }
    }

    @NotNull
    private ProductCategory getProductCategory(Long id) {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setId(id);
        productCategory.setName("Name" + id);
        return productCategory;
    }

    @NotNull
    private ProductCategoryDto getProductCategoryDto(Long id) {
        ProductCategoryDto productCategoryDto = new ProductCategoryDto();
        productCategoryDto.setId(id);
        productCategoryDto.setName("Name" + id);
        return productCategoryDto;
    }
}
