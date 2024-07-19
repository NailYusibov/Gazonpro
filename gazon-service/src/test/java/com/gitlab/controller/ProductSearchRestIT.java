package com.gitlab.controller;

import com.gitlab.TestUtil;
import com.gitlab.dto.ProductDto;
import com.gitlab.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductSearchRestIT extends AbstractIntegrationTest {

    private static final String PRODUCT_URN = "/api/search";
    private static final String PRODUCT_URI = URL + PRODUCT_URN;

    @Autowired
    private ProductService productService;

    @Test
    void should_get_product_by_name() throws Exception {

        ProductDto productDto = productService.save(TestUtil.generateProductDto()).get();

        String expected = objectMapper.writeValueAsString(
                productService.findByNameIgnoreCaseContaining(productDto.getName()));

        mockMvc.perform(get(PRODUCT_URI + "?name=" + productDto.getName()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_no_content_when_search() throws Exception {
        String notFound = "UwUwU";

        mockMvc.perform(get(PRODUCT_URI + "?name=" + notFound))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void should_paginate_when_search() throws Exception{
        int page = 0;
        int size = 5;
        String parameters = "?page=" + page + "&size=" + size;
        List<ProductDto> productDtos = TestUtil.generateProductDtos();
        productDtos.forEach(productService::save);

        mockMvc.perform(get(PRODUCT_URI + "/paginate" + "?name=" + productDtos.get(1).getName() + parameters))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", hasSize(5)));
    }
}