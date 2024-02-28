package com.gitlab.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductCategoryDtoTest extends AbstractDtoTest {

    @Test
    void test_valid_name_length() {
        var productCategoryDto = new ProductCategoryDto();
        productCategoryDto.setName("Name");

        assertTrue(validator.validate(productCategoryDto).isEmpty());
    }

    @Test
    void test_invalid_name_length() {
        var productCategoryDto = new ProductCategoryDto();
        productCategoryDto.setName("");

        assertFalse(validator.validate(productCategoryDto).isEmpty());
    }

    @Test
    void test_invalid_max_name_length() {
        var productCategoryDto = new ProductCategoryDto();
        productCategoryDto.setName("a".repeat(31));

        assertFalse(validator.validate(productCategoryDto).isEmpty());
    }

    @Test
    void test_null_name() {
        var productCategoryDto = new ProductCategoryDto();
        productCategoryDto.setName(null);

        assertFalse(validator.validate(productCategoryDto).isEmpty());
    }

    @Test
    void test_default_message_invalid_length() {
        var productCategoryDto = new ProductCategoryDto();
        productCategoryDto.setName("a".repeat(31));
        String expectedMessage = "Length of ProductCategory name should be between 1 and 30 characters";
        String actualMessage = validator.validate(productCategoryDto).iterator().next().getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void test_default_message_null_text() {
        var productCategoryDto = new ProductCategoryDto();
        productCategoryDto.setName(null);
        String expectedMessage = "ProductCategory name should not be empty";
        String actualMessage = validator.validate(productCategoryDto).iterator().next().getMessage();

        assertEquals(expectedMessage, actualMessage);
    }
}