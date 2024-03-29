package com.gitlab.service;

import com.gitlab.model.Product;
import com.gitlab.model.SelectedProduct;
import com.gitlab.repository.SelectedProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class SelectedProductServiceTest {

    @Mock
    private SelectedProductRepository selectedProductRepository;
    @InjectMocks
    private SelectedProductService selectedProductService;

    @Test
    void should_find_all_selectedProducts() {
        List<SelectedProduct> expectedResult = generateSelectedProducts();
        when(selectedProductRepository.findAll()).thenReturn(generateSelectedProducts());

        List<SelectedProduct> actualResult = selectedProductService.findAll();

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void should_find_selectedProduct_by_id() {
        long id = 1L;
        SelectedProduct expectedResult = generateSelectedProduct();
        when(selectedProductRepository.findById(id)).thenReturn(Optional.of(expectedResult));

        Optional<SelectedProduct> actualResult = selectedProductService.findById(id);

        assertEquals(expectedResult, actualResult.orElse(null));
    }

    @Test
    void should_save_selectedProduct() {
        SelectedProduct expectedResult = generateSelectedProduct();
        when(selectedProductRepository.save(expectedResult)).thenReturn(expectedResult);

        SelectedProduct actualResult = selectedProductService.save(expectedResult);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void should_update_selectedProduct() {
        long id = 2L;
        SelectedProduct selectedProductToUpdate = generateSelectedProduct();


        SelectedProduct selectedProductBeforeUpdate = new SelectedProduct();
        selectedProductBeforeUpdate.setId(id);
        selectedProductBeforeUpdate.setProduct(new Product());
        selectedProductBeforeUpdate.setCount(4);


        SelectedProduct updatedPersonalAddress = generateSelectedProduct();
        updatedPersonalAddress.setId(id);


        when(selectedProductRepository.findById(id)).thenReturn(Optional.of(selectedProductBeforeUpdate));
        when(selectedProductRepository.save(updatedPersonalAddress)).thenReturn(updatedPersonalAddress);

        Optional<SelectedProduct> actualResult = selectedProductService.update(id, selectedProductToUpdate);

        assertEquals(updatedPersonalAddress, actualResult.orElse(null));
    }

    @Test
    void should_not_update_selectedProduct_when_entity_not_found() {
        long id = 1L;
        SelectedProduct selectedProductToUpdate = generateSelectedProduct();

        when(selectedProductRepository.findById(id)).thenReturn(Optional.empty());

        Optional<SelectedProduct> actualResult = selectedProductService.update(id, selectedProductToUpdate);

        verify(selectedProductRepository, never()).save(any());
        assertNull(actualResult.orElse(null));
    }

    @Test
    void should_not_update_count_field_if_null() {
        long id = 1L;
        SelectedProduct selectedProductToUpdate = generateSelectedProduct();
        selectedProductToUpdate.setCount(null);

        SelectedProduct selectedProductBeforeUpdate = generateSelectedProduct();

        when(selectedProductRepository.findById(id)).thenReturn(Optional.of(selectedProductBeforeUpdate));
        when(selectedProductRepository.save(selectedProductBeforeUpdate)).thenReturn(selectedProductBeforeUpdate);

        Optional<SelectedProduct> actualResult = selectedProductService.update(id, selectedProductToUpdate);

        verify(selectedProductRepository).save(selectedProductBeforeUpdate);
        assertNotNull(actualResult.orElse(selectedProductBeforeUpdate).getCount());
    }

    @Test
    void should_not_update_productId_field_if_null() {
        long id = 1L;
        SelectedProduct selectedProductToUpdate = generateSelectedProduct();
        selectedProductToUpdate.setProduct(null);

        SelectedProduct selectedProductBeforeUpdate = generateSelectedProduct();

        when(selectedProductRepository.findById(id)).thenReturn(Optional.of(selectedProductBeforeUpdate));
        when(selectedProductRepository.save(selectedProductBeforeUpdate)).thenReturn(selectedProductBeforeUpdate);

        Optional<SelectedProduct> actualResult = selectedProductService.update(id, selectedProductToUpdate);

        verify(selectedProductRepository).save(selectedProductBeforeUpdate);
        assertNotNull(actualResult.orElse(selectedProductBeforeUpdate).getProduct());
    }

    @Test
    void should_delete_selectedProduct() {
        long id = 1L;
        when(selectedProductRepository.findById(id)).thenReturn(Optional.of(generateSelectedProduct()));

        selectedProductService.delete(id);

        verify(selectedProductRepository).deleteById(id);
    }

    @Test
    void should_not_delete_selectedProduct_when_entity_not_found() {
        long id = 1L;
        when(selectedProductRepository.findById(id)).thenReturn(Optional.empty());

        selectedProductService.delete(id);

        verify(selectedProductRepository, never()).deleteById(anyLong());
    }

    private List<SelectedProduct> generateSelectedProducts() {
        return List.of(
                generateSelectedProduct(1L),
                generateSelectedProduct(2L),
                generateSelectedProduct(3L),
                generateSelectedProduct(4L),
                generateSelectedProduct(5L)
        );
    }

    private SelectedProduct generateSelectedProduct(Long id) {
        SelectedProduct selectedProduct = generateSelectedProduct();
        selectedProduct.setId(id);
        return selectedProduct;
    }

    private SelectedProduct generateSelectedProduct() {
        SelectedProduct selectedProduct = new SelectedProduct();
        selectedProduct.setId(1L);
        selectedProduct.setProduct(new Product());
        selectedProduct.setCount(1);

        return selectedProduct;
    }

    @Test
    void should_update_isSelected_field() {
        long id = 2L;
        SelectedProduct selectedProductToUpdate = generateSelectedProduct();
        selectedProductToUpdate.setIsSelected(true);

        SelectedProduct selectedProductBeforeUpdate = new SelectedProduct();
        selectedProductBeforeUpdate.setId(id);
        selectedProductBeforeUpdate.setProduct(new Product());
        selectedProductBeforeUpdate.setCount(4);
        selectedProductBeforeUpdate.setIsSelected(false);

        SelectedProduct updatedSelectedProduct = generateSelectedProduct();
        updatedSelectedProduct.setId(id);
        updatedSelectedProduct.setIsSelected(true);

        when(selectedProductRepository.findById(id)).thenReturn(Optional.of(selectedProductBeforeUpdate));
        when(selectedProductRepository.save(updatedSelectedProduct)).thenReturn(updatedSelectedProduct);

        Optional<SelectedProduct> actualResult = selectedProductService.update(id, selectedProductToUpdate);

        assertEquals(updatedSelectedProduct, actualResult.orElse(null));
        verify(selectedProductRepository).save(updatedSelectedProduct);
    }

}
