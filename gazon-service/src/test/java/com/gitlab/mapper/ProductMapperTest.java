package com.gitlab.mapper;

import com.gitlab.dto.ProductDto;
import com.gitlab.model.Product;
import com.gitlab.model.Review;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProductMapperTest {

    private final ProductMapper mapper = Mappers.getMapper(ProductMapper.class);


    @Test
    void should_map_product_to_Dto() {
        Product product = getProduct(1L);

        ProductDto dtoTwin = mapper.toDto(product);

        assertNotNull(dtoTwin);
        assertEquals(product.getId(), dtoTwin.getId());
        assertEquals(product.getName(), dtoTwin.getName());
        assertEquals(product.getDescription(), dtoTwin.getDescription());
        assertEquals(product.getCode(), dtoTwin.getCode());
        assertEquals(product.getWeight(), dtoTwin.getWeight());
    }

    @Test
    void should_map_ProductDto_to_Entity() {
        ProductDto productDto = getProductDto(1L);

        Product entityTwin = mapper.toEntity(productDto);

        assertNotNull(entityTwin);
        assertEquals(productDto.getId(), entityTwin.getId());
        assertEquals(productDto.getName(), entityTwin.getName());
        assertEquals(productDto.getDescription(), entityTwin.getDescription());
        assertEquals(productDto.getCode(), entityTwin.getCode());
        assertEquals(productDto.getWeight(), entityTwin.getWeight());
    }

    @Test
    void should_map_productList_to_DtoList() {
        List<Product> productList = List.of(getProduct(1L), getProduct(2L), getProduct(3L));

        List<ProductDto> productDtoList = mapper.toDtoList(productList);

        assertNotNull(productDtoList);
        assertEquals(productList.size(), productList.size());
        for (int i = 0; i < productDtoList.size(); i++) {
            ProductDto dto = productDtoList.get(i);
            Product entity = productList.get(i);
            assertEquals(dto.getId(), entity.getId());
            assertEquals(dto.getName(), entity.getName());
            assertEquals(dto.getDescription(), entity.getDescription());
            assertEquals(dto.getCode(), entity.getCode());
            assertEquals(dto.getWeight(), entity.getWeight());
        }
    }

    @Test
    void should_map_productDtoList_to_EntityList() {
        List<ProductDto> productDtoList = List.of(getProductDto(1L), getProductDto(2L), getProductDto(3L));

        List<Product> productList = mapper.toEntityList(productDtoList);

        assertNotNull(productList);
        assertEquals(productList.size(), productList.size());
        for (int i = 0; i < productList.size(); i++) {
            ProductDto dto = productDtoList.get(i);
            Product entity = productList.get(i);
            assertEquals(dto.getId(), entity.getId());
            assertEquals(dto.getName(), entity.getName());
            assertEquals(dto.getDescription(), entity.getDescription());
            assertEquals(dto.getCode(), entity.getCode());
            assertEquals(dto.getWeight(), entity.getWeight());
        }
    }

    @Test
    void should_map_productSetReview_to_productDtoStringRating() {
        Product product = new Product();
        Set<Review> reviewSet = new HashSet<>();
        Review review = new Review();
        Review review2 = new Review();
        review2.setRating((byte) 4);
        review.setRating((byte) 2);
        reviewSet.add(review);
        reviewSet.add(review2);
        product.setReview(reviewSet);

        double averageRating = reviewSet.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
        String expectedRating = String.format(Locale.ENGLISH, "%.2f", averageRating);

        Assert.assertEquals(expectedRating, "3.00");
    }

    @NotNull
    private Product getProduct(Long id) {
        Product product = new Product();
        product.setId(id);
        product.setName("product" + id);
        product.setDescription("product" + id);
        product.setCode("product" + id);
        product.setWeight(id);
        return product;
    }

    @NotNull
    private ProductDto getProductDto(Long id) {
        ProductDto productDto = new ProductDto();
        productDto.setId(id);
        productDto.setName("product" + id);
        productDto.setDescription("product" + id);
        productDto.setCode("product" + id);
        productDto.setWeight(id);
        return productDto;
    }
}
