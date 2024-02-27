package com.gitlab.controller;

import com.gitlab.dto.ProductDto;
import com.gitlab.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductSearchRestIT extends AbstractIntegrationTest {

    private static final String PRODUCT_URN = "/api/search";
    private static final String PRODUCT_URI = URL + PRODUCT_URN;
    @Autowired
    private ProductService productService;

    @Test
    void should_get_product_by_name() throws Exception {

        ProductDto productDto = new ProductDto();
        productDto.setName("name1");
        productDto.setStockCount(1);
        productDto.setDescription("name");
        productDto.setIsAdult(true);
        productDto.setCode("name");
        productDto.setWeight(1L);
        productDto.setPrice(BigDecimal.ONE);
        productService.save(productDto);

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
}