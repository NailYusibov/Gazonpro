package com.gitlab.controller;

import com.gitlab.TestUtil;
import com.gitlab.dto.SelectedProductDto;
import com.gitlab.mapper.SelectedProductMapper;
import com.gitlab.model.SelectedProduct;
import com.gitlab.service.ProductService;
import com.gitlab.service.SelectedProductService;
import com.gitlab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.hamcrest.CoreMatchers.equalTo;
import static org.testcontainers.shaded.org.hamcrest.MatcherAssert.assertThat;

class SelectedProductRestControllerIT extends AbstractIntegrationTest {

    private static final String SELECTED_PRODUCT_URN = "/api/selected-product";
    private static final String SELECTED_PRODUCT_URI = URL + SELECTED_PRODUCT_URN;

    @Autowired
    private SelectedProductService selectedProductService;
    @Autowired
    private ProductService productService;
    @Autowired
    private SelectedProductMapper selectedProductMapper;
    @Autowired
    private UserService userService;

    @Test
    @Transactional(readOnly = true)
    void should_get_all_selectedProducts() throws Exception {

        var response = selectedProductService.getPage(null, null);
        var expected = objectMapper.writeValueAsString(selectedProductMapper.toDtoList(response.getContent()));

        mockMvc.perform(get(SELECTED_PRODUCT_URI))
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

        var response = selectedProductService.getPage(page, size);
        assertFalse(response.getContent().isEmpty());

        var expected = objectMapper.writeValueAsString(selectedProductMapper.toDtoList(response.getContent()));

        mockMvc.perform(get(SELECTED_PRODUCT_URI + parameters))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_get_page_with_incorrect_parameters() throws Exception {
        int page = 0;
        int size = -2;
        String parameters = "?page=" + page + "&size=" + size;

        mockMvc.perform(get(SELECTED_PRODUCT_URI + parameters))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void should_get_page_without_content() throws Exception {
        int page = 10;
        int size = 100;
        String parameters = "?page=" + page + "&size=" + size;

        mockMvc.perform(get(SELECTED_PRODUCT_URI + parameters))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void should_get_selectedProduct_by_id() throws Exception {
        SelectedProductDto selectedProductDto = selectedProductService.saveDto
                (TestUtil.generateSelectedProductDto(
                        productService.save(TestUtil.generateProductDto()).get().getId(),
                        userService.saveDto(TestUtil.generateUserDto()).getId()));

        long id = selectedProductDto.getId();

        SelectedProduct selectedProduct = selectedProductMapper.toEntity(selectedProductDto);
        selectedProductMapper.calculatedUnmappedFields(selectedProductDto, selectedProduct);

        String expected = objectMapper.writeValueAsString(selectedProductDto);

        mockMvc.perform(get(SELECTED_PRODUCT_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_return_not_found_when_get_selectedProduct_by_non_existent_id() throws Exception {
        long id = 9999L;

        mockMvc.perform(get(SELECTED_PRODUCT_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_create_selectedProduct() throws Exception {
        SelectedProductDto selectedProductDto = TestUtil.generateSelectedProductDto(
                productService.save(TestUtil.generateProductDto()).get().getId(),
                userService.saveDto(TestUtil.generateUserDto()).getId());

        String jsonSelectedProductDto = objectMapper.writeValueAsString(selectedProductDto);

        mockMvc.perform(post(SELECTED_PRODUCT_URI)
                        .content(jsonSelectedProductDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void should_update_selectedProduct_by_id() throws Exception {
        SelectedProductDto selectedProductDto = selectedProductService.saveDto(TestUtil.generateSelectedProductDto(
                productService.save(TestUtil.generateProductDto()).get().getId(),
                userService.saveDto(TestUtil.generateUserDto()).getId()));

        long id = selectedProductDto.getId();

        int numberOfEntitiesExpected = selectedProductService.findAll().size();
        selectedProductDto.setCount(99);

        String jsonSelectedProductDto = objectMapper.writeValueAsString(selectedProductDto);
        String expected = objectMapper.writeValueAsString(selectedProductDto);

        mockMvc.perform(patch(SELECTED_PRODUCT_URI + "/{id}", id)
                        .content(jsonSelectedProductDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected))
                .andExpect(result -> assertThat(selectedProductService.findAll().size(),
                        equalTo(numberOfEntitiesExpected)));
    }

    @Test
    void should_return_not_found_when_update_selectedProduct_by_non_existent_id() throws Exception {
        SelectedProductDto selectedProductDto = selectedProductService.saveDto(TestUtil.generateSelectedProductDto(
                productService.save(TestUtil.generateProductDto()).get().getId(),
                userService.saveDto(TestUtil.generateUserDto()).getId()));

        long id = 9999L;

        String jsonSelectedProductDto = objectMapper.writeValueAsString(selectedProductDto);

        mockMvc.perform(patch(SELECTED_PRODUCT_URI + "/{id}", id)
                        .content(jsonSelectedProductDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_delete_selectedProduct_by_id() throws Exception {
        long id = selectedProductService.saveDto(
                        TestUtil.generateSelectedProductDto(
                                productService.save(TestUtil.generateProductDto()).get().getId(),
                                userService.saveDto(TestUtil.generateUserDto()).getId()))
                .getId();

        mockMvc.perform(delete(SELECTED_PRODUCT_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(delete(SELECTED_PRODUCT_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_use_user_assigned_id_in_database_for_selected_product() throws Exception {
        SelectedProductDto selectedProductDto = TestUtil.generateSelectedProductDto(
                productService.save(TestUtil.generateProductDto()).get().getId(),
                userService.saveDto(TestUtil.generateUserDto()).getId());

        selectedProductDto.setId(9999L);
        String jsonSelectedProductDto = objectMapper.writeValueAsString(selectedProductDto);

        MockHttpServletResponse response = mockMvc.perform(post(SELECTED_PRODUCT_URI)
                        .content(jsonSelectedProductDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        SelectedProductDto createdSelectedProductDto = objectMapper.readValue(response.getContentAsString(), SelectedProductDto.class);
        Assertions.assertNotEquals(selectedProductDto.getId(), createdSelectedProductDto.getId());
    }
}