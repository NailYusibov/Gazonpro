package com.gitlab.controller;

import com.gitlab.dto.ShoppingCartDto;
import com.gitlab.mapper.ShoppingCartMapper;
import com.gitlab.service.ShoppingCartService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.hamcrest.CoreMatchers.equalTo;
import static org.testcontainers.shaded.org.hamcrest.MatcherAssert.assertThat;

class ShoppingCartRestControllerIT extends AbstractIntegrationTest {

    private static final String SHOPPING_CART_URN = "/api/shopping-cart";
    private static final String SHOPPING_CART_URI = URL + SHOPPING_CART_URN;

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Test
    @Transactional(readOnly = true)
    void should_get_all_shoppingCarts() throws Exception {

        var response = shoppingCartService.getPage(null, null);
        var expected = objectMapper.writeValueAsString(shoppingCartMapper.toDtoList(response.getContent()));

        mockMvc.perform(get(SHOPPING_CART_URI))
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

        var response = shoppingCartService.getPage(page, size);
        assertFalse(response.getContent().isEmpty());

        var expected = objectMapper.writeValueAsString(shoppingCartMapper.toDtoList(response.getContent()));

        mockMvc.perform(get(SHOPPING_CART_URI + parameters))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_get_page_with_incorrect_parameters() throws Exception {
        int page = 0;
        int size = -2;
        String parameters = "?page=" + page + "&size=" + size;

        mockMvc.perform(get(SHOPPING_CART_URI + parameters))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void should_get_page_without_content() throws Exception {
        int page = 10;
        int size = 100;
        String parameters = "?page=" + page + "&size=" + size;

        mockMvc.perform(get(SHOPPING_CART_URI + parameters))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Transactional
    @Test
    void should_return_not_found_when_get_shoppingCart_by_non_existent_id() throws Exception {
        long id = 10L;
        mockMvc.perform(get(SHOPPING_CART_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_create_shoppingCart() throws Exception {
        ShoppingCartDto shoppingCartDto = generateShoppingCartDto();
        String jsonShoppingCartDto = objectMapper.writeValueAsString(shoppingCartDto);

        mockMvc.perform(post(SHOPPING_CART_URI)
                        .content(jsonShoppingCartDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Transactional
    @Test
    void should_update_shoppingCart_by_id() throws Exception {
        long id = 10L;
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(id);
        shoppingCartDto.setUserId(1L);

        ShoppingCartDto saved = shoppingCartService.saveDto(shoppingCartDto);
        int numberOfEntitiesExpected = shoppingCartService.findAll().size();

        ShoppingCartDto updated = new ShoppingCartDto();
        updated.setId(saved.getId());
        updated.setUserId(1L);
        String jsonShoppingCartDto = objectMapper.writeValueAsString(updated);

        String expected = objectMapper.writeValueAsString(updated);

        mockMvc.perform(patch(SHOPPING_CART_URI + "/{id}", saved.getId())
                        .content(jsonShoppingCartDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected))
                .andExpect(result -> assertThat(shoppingCartService.findAll().size(), equalTo(numberOfEntitiesExpected)));
    }

    @Transactional
    @Test
    void should_return_not_found_when_update_shoppingCart_by_non_existent_id() throws Exception {
        long id = 10L;
        ShoppingCartDto shoppingCartDto = generateShoppingCartDto();
        String jsonShoppingCartDto = objectMapper.writeValueAsString(shoppingCartDto);

        mockMvc.perform(patch(SHOPPING_CART_URI + "/{id}", id)
                        .content(jsonShoppingCartDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_get_shoppingCart_by_id() throws Exception {
        long id = 1L;
        var shoppingCart = shoppingCartService.findById(id).orElse(null);
        var shoppingCartDto = shoppingCartMapper.toDto(shoppingCart);
        String expected = objectMapper.writeValueAsString(shoppingCartDto);

        mockMvc.perform(get(SHOPPING_CART_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Transactional
    @Test
    void should_delete_shoppingCart_by_id() throws Exception {
        long id = 3L;

        mockMvc.perform(delete(SHOPPING_CART_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(get(SHOPPING_CART_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private ShoppingCartDto generateShoppingCartDto() {
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setUserId(3L);
        shoppingCartDto.setSum(BigDecimal.valueOf(100));
        shoppingCartDto.setTotalWeight(500L);

        return shoppingCartDto;
    }

    @Test
    void should_use_user_assigned_id_in_database_for_shopping_cart() throws Exception {
        ShoppingCartDto shoppingCartDto = generateShoppingCartDto();
        shoppingCartDto.setId(9999L);

        String jsonShoppingCartDto = objectMapper.writeValueAsString(shoppingCartDto);
        MockHttpServletResponse response = mockMvc.perform(post(SHOPPING_CART_URI)
                        .content(jsonShoppingCartDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        ShoppingCartDto createdShoppingCartDto = objectMapper.readValue(response.getContentAsString(), ShoppingCartDto.class);
        Assertions.assertNotEquals(shoppingCartDto.getId(), createdShoppingCartDto.getId());
    }

}