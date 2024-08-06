package com.gitlab.service;

import com.gitlab.dto.ProductDto;
import com.gitlab.enums.EntityStatus;
import com.gitlab.mapper.ProductMapper;
import com.gitlab.model.*;
import com.gitlab.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProductService productService;

    private User user;
    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFavouriteProducts(new HashSet<>());
        product = new Product();
        product.setId(1L);
        productDto = new ProductDto();
        productDto.setId(1L);
    }

    @Test
    void should_find_all_products() {
        List<ProductDto> expectedResult = generateProductDtos();
        when(productRepository.findAll()).thenReturn(generateProducts());
        when(productMapper.toDtoList(generateProducts())).thenReturn(generateProductDtos());

        List<ProductDto> actualResult = productService.findAll();

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void should_find_all_products_by_storeId_when_not_null() {
        Long storeId = 1L;

        List<ProductDto> expectedResult = generateProductDtos();
        when(productRepository.findAll(storeId)).thenReturn(generateProducts());
        when(productMapper.toDtoList(generateProducts())).thenReturn(generateProductDtos());

        List<ProductDto> actualResult = productService.findAllByStoreId(storeId);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    void should_find_all_products_by_storeId_when_null() {
        List<ProductDto> expectedResult = generateProductDtos();
        when(productRepository.findAll()).thenReturn(generateProducts());
        when(productMapper.toDtoList(generateProducts())).thenReturn(generateProductDtos());

        List<ProductDto> actualResult = productService.findAllByStoreId(null);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    void should_find_product_by_id() {

        Product expectedResult = generateProduct();
        Long id = expectedResult.getId();
        when(productRepository.findById(id)).thenReturn(Optional.of(expectedResult));

        Optional<Product> actualResult = productService.findById(id);

        assertEquals(expectedResult, actualResult.orElse(null));
    }

    @Test
    void should_find_productDto_by_id() {
        ProductDto expectedResult = generateProductDto();
        Long id = expectedResult.getId();

        when(productRepository.findById(id)).thenReturn(Optional.of(generateProduct()));
        when(productMapper.toDto(generateProduct())).thenReturn(expectedResult);

        Optional<ProductDto> actualResult = productService.findByIdDto(id);

        assertEquals(expectedResult, actualResult.orElse(null));
    }

    @Test
    void should_find_products_with_pagination_of_not_null_fields() {
        Integer page = 0;
        Integer size = 2;

        List<Product> productList = new ArrayList<>();
        productList.add(generateProduct(1L));
        productList.add(generateProduct(2L));

        Page<Product> productPage = new PageImpl<>(productList);

        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);
        when(productMapper.toDto(generateProduct(1L))).thenReturn(generateProductDto(1L));
        when(productMapper.toDto(generateProduct(2L))).thenReturn(generateProductDto(2L));

        Page<ProductDto> result = productService.getPage(page, size);

        verify(productRepository, times(1)).findAll(any(Pageable.class));
        verify(productMapper, times(productList.size())).toDto(any(Product.class));
    }

    @Test
    void should_find_products_with_pagination_of_null_parameter_fields() {
        Page<ProductDto> expectedResult = new PageImpl<>(generateProductDtos());
        when(productService.findAll()).thenReturn(generateProductDtos());
        Page<ProductDto> actualResult = productService.getPage(null, null);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void should_find_products_with_pagination_of_null_fields_when_products_not_found() {
        when(productService.findAll()).thenReturn(Collections.emptyList());
        Page<ProductDto> actualResult = productService.getPage(null, null);

        assertEquals(Page.empty(), actualResult);
    }

    @Test
    void should_find_products_with_pagination_of_incorrect_parameter_fields() {
        Page<ProductDto> actualResult1 = productService.getPage(0, 0);
        Page<ProductDto> actualResult2 = productService.getPage(-1, 2);
        Page<ProductDto> actualResult3 = productService.getPage(-1, 0);

        assertEquals(Page.empty(), actualResult1);
        assertEquals(Page.empty(), actualResult2);
        assertEquals(Page.empty(), actualResult3);
    }

    @Test
    void should_save_product() {
        Optional<ProductDto> expectedResult = Optional.of(generateProductDto());

        when(productRepository.save(any(Product.class))).thenReturn(generateProduct());
        when(productMapper.toEntity(any(ProductDto.class))).thenReturn(generateProduct());
        when(productMapper.toDto(any(Product.class))).thenReturn(generateProductDto());

        Optional<ProductDto> actualResult = productService.save(generateProductDto());

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void should_update_product() {
        ProductDto inputProductDto = generateProductDto();
        Long id = inputProductDto.getId();

        Product productBeforeUpdate = new Product();
        productBeforeUpdate.setId(id);
        productBeforeUpdate.setName("old name");
        productBeforeUpdate.setDescription("old");
        productBeforeUpdate.setEntityStatus(EntityStatus.ACTIVE);

        Product productAfterUpdate = generateProduct();
        productAfterUpdate.setId(id);

        when(productRepository.findById(id)).thenReturn(Optional.of(productBeforeUpdate));
        when(productRepository.save(productAfterUpdate)).thenReturn(productAfterUpdate);
        when(productMapper.toDto(productAfterUpdate)).thenReturn(generateProductDto());

        Optional<ProductDto> actualResult = productService.update(id, inputProductDto);

        assertEquals(inputProductDto, actualResult.orElse(null));

    }

    @Test
    void should_not_update_product_when_entity_not_found() {
        long id = 4L;
        ProductDto productToUpdateWith = generateProductDto();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        Optional<ProductDto> actualResult = productService.update(id, productToUpdateWith);

        verify(productRepository, never()).save(any());
        assertNull(actualResult.orElse(null));
    }

    @Test
    void should_delete_product() {
        Product activeProduct = generateProduct();
        activeProduct.setEntityStatus(EntityStatus.ACTIVE);

        Product deletedProduct = generateProduct();
        deletedProduct.setEntityStatus(EntityStatus.DELETED);

        Long id = activeProduct.getId();

        when(productRepository.findById(id)).thenReturn(Optional.of(activeProduct));
        when(productRepository.save(activeProduct)).thenReturn(deletedProduct);
        when(productMapper.toDto(deletedProduct)).thenReturn(generateProductDto(id));

        productService.delete(id);

        verify(productRepository).save(activeProduct);
    }

    @Test
    void should_not_delete_product_when_entity_not_found() {
        long id = 1L;
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        productService.delete(id);

        verify(productRepository, never()).save(any());
    }

    @Test
    void should_not_updated_product_name_field_if_null() {
        long id = 1L;
        ProductDto productToUpdateWith = generateProductDto();
        productToUpdateWith.setName(null);

        Product productBeforeUpdate = generateProduct();

        when(productRepository.findById(id)).thenReturn(Optional.of(productBeforeUpdate));
        when(productRepository.save(productBeforeUpdate)).thenReturn(productBeforeUpdate);
        when(productMapper.toDto(any())).thenReturn(generateProductDto());

        Optional<ProductDto> actualResult = productService.update(id, productToUpdateWith);

        verify(productRepository).save(productBeforeUpdate);
        assertNotNull(actualResult.orElse(productToUpdateWith).getName());
    }

    @Test
    void should_not_updated_product_stockCount_field_if_null() {
        long id = 1L;
        ProductDto productToUpdateWith = generateProductDto();
        productToUpdateWith.setStockCount(null);

        Product productBeforeUpdate = generateProduct();

        when(productRepository.findById(id)).thenReturn(Optional.of(productBeforeUpdate));
        when(productRepository.save(productBeforeUpdate)).thenReturn(productBeforeUpdate);
        when(productMapper.toDto(any())).thenReturn(generateProductDto());

        Optional<ProductDto> actualResult = productService.update(id, productToUpdateWith);

        verify(productRepository).save(productBeforeUpdate);
        assertNotNull(actualResult.orElse(productToUpdateWith).getStockCount());
    }

    @Test
    void should_not_updated_product_description_field_if_null() {
        long id = 1L;
        ProductDto productToUpdateWith = generateProductDto();
        productToUpdateWith.setDescription(null);

        Product productBeforeUpdate = generateProduct();

        when(productRepository.findById(id)).thenReturn(Optional.of(productBeforeUpdate));
        when(productRepository.save(productBeforeUpdate)).thenReturn(productBeforeUpdate);
        when(productMapper.toDto(any())).thenReturn(generateProductDto());

        Optional<ProductDto> actualResult = productService.update(id, productToUpdateWith);

        verify(productRepository).save(productBeforeUpdate);
        assertNotNull(actualResult.orElse(productToUpdateWith).getDescription());
    }

    @Test
    void shouldAddFavouriteProductWhenProductExistsAndNotAlreadyFavourite() {
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        Optional<Product> result = productService.addFavouriteProduct(1L);

        assertTrue(result.isPresent());
        assertEquals(product, result.get());
        assertTrue(user.getFavouriteProducts().contains(product));
        verify(userService).save(user);
    }

    @Test
    void shouldDeleteFavouriteProductWhenProductExists() {
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        user.getFavouriteProducts().add(product);

        Optional<Product> result = productService.removeFavouriteProduct(product.getId());

        assertTrue(result.isPresent());
        assertFalse(user.getFavouriteProducts().contains(product));
        verify(userService).save(user);
    }

    @Test
    public void should_get_all_favourite_products() {

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(productMapper.toDtoList(anyList())).thenReturn(Collections.singletonList(productDto));

        List<ProductDto> result = productService.getFavouriteProducts();

        assertEquals(1, result.size());
        assertEquals(productDto.getId(), result.get(0).getId());

        verify(productMapper).toDtoList(anyList());
    }

    private Product generateProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("name1");
        product.setStockCount(2);
        product.setDescription("name");
        product.setIsAdult(true);
        product.setCode("name");
        product.setWeight(2L);
        product.setPrice(BigDecimal.ONE);
        product.setEntityStatus(EntityStatus.ACTIVE);
        return product;
    }

    private Product generateProduct(Long id) {
        Product product = generateProduct();
        product.setId(id);
        return product;
    }

    private List<Product> generateProducts() {
        return List.of(
                generateProduct(1L),
                generateProduct(2L),
                generateProduct(3L),
                generateProduct(4L),
                generateProduct(5L));
    }

    private ProductDto generateProductDto() {
        ProductDto dto = new ProductDto();
        dto.setId(1L);
        dto.setName("name1");
        dto.setStockCount(2);
        dto.setDescription("name");
        dto.setIsAdult(true);
        dto.setCode("name");
        dto.setWeight(2L);
        dto.setPrice(BigDecimal.ONE);
        dto.setStoreId(1L);
        dto.setRating("8.0");

        return dto;
    }

    private ProductDto generateProductDto(Long id) {
        ProductDto dto = new ProductDto();
        dto.setId(id);
        return dto;
    }

    private List<ProductDto> generateProductDtos() {
        return List.of(
                generateProductDto(1L),
                generateProductDto(2L),
                generateProductDto(3L),
                generateProductDto(4L),
                generateProductDto(5L)
        );
    }
}
