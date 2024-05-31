package com.gitlab.controller;


import com.gitlab.TestUtil;
import com.gitlab.dto.ProductCategoryDto;
import com.gitlab.service.ProductCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.hamcrest.CoreMatchers.equalTo;
import static org.testcontainers.shaded.org.hamcrest.MatcherAssert.assertThat;

class ProductCategoryControllerIT extends AbstractIntegrationTest {

    private static final String PRODUCTCATEGORY_URN = "/api/category";
    private static final String PRODUCTCATEGORY_URI = URL + PRODUCTCATEGORY_URN;
    @Autowired
    private ProductCategoryService productCategoryService;


    @Test
    @Transactional(readOnly = true)
    void should_get_all_productCategory() throws Exception {
        String expected = objectMapper.writeValueAsString(
                new ArrayList<>(productCategoryService
                        .getPageDto(null, null)
                        .stream()
                        .collect(Collectors.toList()))
        );

        mockMvc.perform(get(PRODUCTCATEGORY_URI))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    @Transactional(readOnly = true)
    void should_get_page() throws Exception {
        int page = 0;
        int size = 2;
        String parameters = "?page=" + page + "&size=" + size;

        var response = productCategoryService.getPageDto(page, size);
        assertFalse(response.getContent().isEmpty());

        var expected = objectMapper.writeValueAsString(response
                .stream().toList());

        mockMvc.perform(get(PRODUCTCATEGORY_URI + parameters))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_get_productCategory_by_id() throws Exception {
        long id;

        ProductCategoryDto productCategoryDto = TestUtil.generateProductCategoryDto();

        id = productCategoryService.saveDto(productCategoryDto).getId();

        String expected = objectMapper.writeValueAsString(
                productCategoryService
                        .findByIdDto(id)
                        .orElse(null)
        );

        mockMvc.perform(get(PRODUCTCATEGORY_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_return_not_found_when_get_productCategory_by_non_existent_id() throws Exception {
        long id = 9999L;

        mockMvc.perform(get(PRODUCTCATEGORY_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_create_productCategory() throws Exception {
        ProductCategoryDto productCategoryDto = TestUtil.generateProductCategoryDto();

        String jsonProductCategoryDto = objectMapper.writeValueAsString(productCategoryDto);

        mockMvc.perform(post(PRODUCTCATEGORY_URI)
                        .content(jsonProductCategoryDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void should_update_productCategory_by_id() throws Exception {
        long id;
        int numberOfEntitiesExpected;

        ProductCategoryDto productCategoryDto = TestUtil.generateProductCategoryDto();

        id = productCategoryService.saveDto(productCategoryDto).getId();
        numberOfEntitiesExpected = productCategoryService.findAllDto().size();

        productCategoryDto.setId(id);
        productCategoryDto.setName("updateName");

        String jsonProductCategoryDto = objectMapper.writeValueAsString(productCategoryDto);
        String expected = objectMapper.writeValueAsString(productCategoryDto);

        mockMvc.perform(patch(PRODUCTCATEGORY_URI + "/{id}", id)
                        .content(jsonProductCategoryDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected))
                .andExpect(result -> assertThat(productCategoryService.findAllDto().size(),
                        equalTo(numberOfEntitiesExpected)));

        productCategoryService.delete(id);
    }

    @Test
    void should_return_not_found_when_update_productCategory_by_non_existent_id() throws Exception {
        long id = 9999L;

        ProductCategoryDto productCategoryDto = TestUtil.generateProductCategoryDto();
        productCategoryDto.setName("testName");

        String jsonProductCategoryDto = objectMapper.writeValueAsString(productCategoryDto);

        mockMvc.perform(patch(PRODUCTCATEGORY_URI + "/{id}", id)
                        .content(jsonProductCategoryDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_delete_productCategory_by_id() throws Exception {
        long id = productCategoryService.saveDto(TestUtil.generateProductCategoryDto()).getId();

        mockMvc.perform(delete(PRODUCTCATEGORY_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(get(PRODUCTCATEGORY_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}