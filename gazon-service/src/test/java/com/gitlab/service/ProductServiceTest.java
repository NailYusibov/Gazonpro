//package com.gitlab.service;
//
//import com.gitlab.enums.EntityStatus;
//import com.gitlab.model.Product;
//import com.gitlab.repository.ProductRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ProductServiceTest {
//
//    @Mock
//    private ProductRepository productRepository;
//    @InjectMocks
//    private ProductService productService;
//
////    @Test
////    void should_find_all_products() {
////        List<Product> expectedResult = generateProducts();
////        when(productRepository.findAll()).thenReturn(generateProducts());
////
////        List<Product> actualResult = productService.findAll();
////
////        assertEquals(expectedResult, actualResult);
////    }
//
//    @Test
//    void should_find_product_by_id() {
//
//        Product expectedResult = generateProduct();
//        Long id = expectedResult.getId();
//        when(productRepository.findById(id)).thenReturn(Optional.of(expectedResult));
//
//        Optional<Product> actualResult = productService.findById(id);
//
//        assertEquals(expectedResult, actualResult.orElse(null));
//    }
//
////    @Test
////    void should_save_product() {
////        Product expectedResult = generateProduct();
////        when(productRepository.save(expectedResult)).thenReturn(expectedResult);
////
////        Product actualResult = productService.save(expectedResult);
////
////        assertEquals(expectedResult, actualResult);
////    }
//
//    @Test
//    void should_update_product() {
//        Product productToUpdateWith = generateProduct();
//        Long id = productToUpdateWith.getId();
//
//        Product productBeforeUpdate = new Product();
//        productBeforeUpdate.setId(id);
//        productBeforeUpdate.setName("old name");
//        productBeforeUpdate.setDescription("old");
//        productBeforeUpdate.setEntityStatus(EntityStatus.ACTIVE);
//
//        Product productFromFuture = generateProduct();
//        productFromFuture.setId(id);
//
//        when(productRepository.findById(id)).thenReturn(Optional.of(productBeforeUpdate));
//        when(productRepository.save(productFromFuture)).thenReturn(productFromFuture);
//
//        Optional<Product> actualResult = productService.update(id, productToUpdateWith);
//
//        assertEquals(productFromFuture, actualResult.orElse(null));
//    }
//
//    @Test
//    void should_not_update_product_when_entity_not_found() {
//        long id = 4L;
//        Product productToUpdateWith = generateProduct();
//
//
//        when(productRepository.findById(id)).thenReturn(Optional.empty());
//
//        Optional<Product> actualResult = productService.update(id, productToUpdateWith);
//
//        verify(productRepository, never()).save(any());
//        assertNull(actualResult.orElse(null));
//    }
//
//    @Test
//    void should_delete_product() {
//        long id = 1L;
//
//        when(productRepository.findById(id)).thenReturn(Optional.of(generateProduct()));
//
//        productService.delete(id);
//
//        verify(productRepository).deleteById(id);
//    }
//
//    @Test
//    void should_not_delete_product_when_entity_not_found() {
//        long id = 1L;
//        when(productRepository.findById(id)).thenReturn(Optional.empty());
//
//        productService.delete(id);
//
//        verify(productRepository, never()).deleteById(anyLong());
//    }
//
//    @Test
//    void should_not_updated_product_name_field_if_null() {
//        long id = 1L;
//        Product productToUpdateWith = generateProduct();
//        productToUpdateWith.setName(null);
//
//        Product productBeforeUpdate = generateProduct();
//
//        when(productRepository.findById(id)).thenReturn(Optional.of(productBeforeUpdate));
//        when(productRepository.save(productBeforeUpdate)).thenReturn(productBeforeUpdate);
//
//        Optional<Product> actualResult = productService.update(id, productToUpdateWith);
//
//        verify(productRepository).save(productBeforeUpdate);
//        assertNotNull(actualResult.orElse(productBeforeUpdate).getName());
//    }
//
//    @Test
//    void should_not_updated_product_stockCount_field_if_null() {
//        long id = 1L;
//        Product productToUpdateWith = generateProduct();
//        productToUpdateWith.setStockCount(null);
//
//        Product productBeforeUpdate = generateProduct();
//
//        when(productRepository.findById(id)).thenReturn(Optional.of(productBeforeUpdate));
//        when(productRepository.save(productBeforeUpdate)).thenReturn(productBeforeUpdate);
//
//        Optional<Product> actualResult = productService.update(id, productToUpdateWith);
//
//        verify(productRepository).save(productBeforeUpdate);
//        assertNotNull(actualResult.orElse(productBeforeUpdate).getStockCount());
//    }
//
//    @Test
//    void should_not_updated_product_description_field_if_null() {
//        long id = 1L;
//        Product productToUpdateWith = generateProduct();
//        productToUpdateWith.setDescription(null);
//
//        Product productBeforeUpdate = generateProduct();
//
//        when(productRepository.findById(id)).thenReturn(Optional.of(productBeforeUpdate));
//        when(productRepository.save(productBeforeUpdate)).thenReturn(productBeforeUpdate);
//
//        Optional<Product> actualResult = productService.update(id, productToUpdateWith);
//
//        verify(productRepository).save(productBeforeUpdate);
//        assertNotNull(actualResult.orElse(productBeforeUpdate).getDescription());
//    }
//
//    private List<Product> generateProducts() {
//        return List.of(
//                generateProduct(1L),
//                generateProduct(2L),
//                generateProduct(3L),
//                generateProduct(4L),
//                generateProduct(5L));
//    }
//
//    private Product generateProduct(Long id) {
//        Product product = generateProduct();
//        product.setId(id);
//        return product;
//    }
//
//    private Product generateProduct() {
//        Product product = new Product();
//        product.setId(1L);
//        product.setName("name1");
//        product.setStockCount(2);
//        product.setDescription("name");
//        product.setIsAdult(true);
//        product.setCode("name");
//        product.setWeight(2L);
//        product.setPrice(BigDecimal.ONE);
//        product.setEntityStatus(EntityStatus.ACTIVE);
//        return product;
//    }
//
//}
