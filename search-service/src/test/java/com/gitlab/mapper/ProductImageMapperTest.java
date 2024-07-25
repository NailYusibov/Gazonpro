package com.gitlab.mapper;

import com.gitlab.controller.AbstractIntegrationTest;
import com.gitlab.dto.ProductImageDto;
import com.gitlab.model.Product;
import com.gitlab.model.ProductImage;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProductImageMapperTest extends AbstractIntegrationTest {

    @Autowired
    private ProductImageMapper mapper;


    @Test
    void should_map_productImage_to_Dto() {
        ProductImage productImage = getProductImage(1L);

        ProductImageDto dtoTwin = mapper.toDto(productImage);

        assertNotNull(dtoTwin);
        assertEquals(productImage.getId(), dtoTwin.getId());
        assertEquals(productImage.getSomeProduct().getId(), dtoTwin.getProductId());
        assertEquals(productImage.getName(), dtoTwin.getName());
        assertEquals(Arrays.toString(productImage.getData()), Arrays.toString(dtoTwin.getData()));
    }

    @Test
    void should_map_productImageDto_to_Entity() {

        ProductImageDto productImageDto = getProductImageDto(1L);

        ProductImage entityTwin = mapper.toEntity(productImageDto);

        assertNotNull(entityTwin);
        assertEquals(productImageDto.getId(), entityTwin.getId());
        assertEquals(productImageDto.getProductId(), entityTwin.getSomeProduct().getId());
        assertEquals(productImageDto.getName(), entityTwin.getName());
        assertEquals(Arrays.toString(productImageDto.getData()), Arrays.toString(entityTwin.getData()));
    }

    @Test
    void should_map_productImageList_to_DtoList() {
        List<ProductImage> productImageList = List.of(getProductImage(1L), getProductImage(2L), getProductImage(3L));

        List<ProductImageDto> productImageDtoList = mapper.toDtoList(productImageList);

        assertNotNull(productImageDtoList);
        assertEquals(productImageList.size(), productImageList.size());
        for (int i = 0; i < productImageDtoList.size(); i++) {
            ProductImageDto dto = productImageDtoList.get(i);
            ProductImage entity = productImageList.get(i);
            assertEquals(dto.getId(), entity.getId());
            assertEquals(dto.getProductId(), entity.getSomeProduct().getId());
            assertEquals(dto.getName(), entity.getName());
            assertEquals(Arrays.toString(dto.getData()), Arrays.toString(entity.getData()));
        }
    }

    @Test
    void should_map_productImageDtoList_to_EntityList() {
        List<ProductImageDto> productImageDtoList = List.of(getProductImageDto(1L), getProductImageDto(2L), getProductImageDto(3L));

        List<ProductImage> productImageList = mapper.toEntityList(productImageDtoList);

        assertNotNull(productImageList);
        assertEquals(productImageList.size(), productImageList.size());
        for (int i = 0; i < productImageList.size(); i++) {
            ProductImageDto dto = productImageDtoList.get(i);
            ProductImage entity = productImageList.get(i);
            assertEquals(dto.getId(), entity.getId());
            assertEquals(dto.getProductId(), entity.getSomeProduct().getId());
            assertEquals(dto.getName(), entity.getName());
            assertEquals(Arrays.toString(dto.getData()), Arrays.toString(entity.getData()));
        }
    }

    @NotNull
    private ProductImage getProductImage(Long id) {
        ProductImage productImage = new ProductImage();
        Product product = new Product();
        product.setId(id);
        productImage.setSomeProduct(product);
        productImage.setName("product" + id);
        productImage.setData(new byte[]{(byte)(id % 128)});
        return productImage;
    }
    
    @NotNull
    private ProductImageDto getProductImageDto(Long id) {
        ProductImageDto productImageDto = new ProductImageDto();
        productImageDto.setProductId(id);
        productImageDto.setName("product" + id);
        productImageDto.setData(new byte[]{(byte)(id % 128)});
        return productImageDto;
    }
}
