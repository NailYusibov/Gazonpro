package com.gitlab.controller;

import com.gitlab.TestUtil;
import com.gitlab.dto.SelectedProductDto;
import com.gitlab.dto.ShoppingCartDto;
import com.gitlab.mapper.ShoppingCartMapper;
import com.gitlab.service.ProductService;
import com.gitlab.service.ShoppingCartService;
import com.gitlab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

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
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;

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

    @Test
    @Transactional
    void should_return_not_found_when_get_shoppingCart_by_non_existent_id() throws Exception {
        long id = 9999L;

        mockMvc.perform(get(SHOPPING_CART_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_create_shoppingCart() throws Exception {
        ShoppingCartDto shoppingCartDto = TestUtil.generateShoppingCartDto(
                userService.saveDto(TestUtil.generateUserDto()).getId());

        String jsonShoppingCartDto = objectMapper.writeValueAsString(shoppingCartDto);

        mockMvc.perform(post(SHOPPING_CART_URI)
                        .content(jsonShoppingCartDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @Transactional
    void should_update_shoppingCart_by_id() throws Exception {
        long id;
        ShoppingCartDto shoppingCartDto = TestUtil.generateShoppingCartDto(
                userService.saveDto(TestUtil.generateUserDto()).getId());

        ShoppingCartDto saved = shoppingCartService.saveDto(shoppingCartDto);

        int numberOfEntitiesExpected = shoppingCartService.findAll().size();
        id = saved.getId();
        saved.setSelectedProducts(Set.of(new SelectedProductDto()));

        String jsonShoppingCartDto = objectMapper.writeValueAsString(saved);
        String expected = objectMapper.writeValueAsString(saved);

        mockMvc.perform(patch(SHOPPING_CART_URI + "/{id}", id)
                        .content(jsonShoppingCartDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected))
                .andExpect(result -> assertThat(shoppingCartService.findAll().size(), equalTo(numberOfEntitiesExpected)));
    }

    @Test
    @Transactional
    void should_return_not_found_when_update_shoppingCart_by_non_existent_id() throws Exception {
        long id = 9999L;

        ShoppingCartDto shoppingCartDto = TestUtil.generateShoppingCartDto(
                userService.saveDto(TestUtil.generateUserDto()).getId());

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
        long id;

        ShoppingCartDto shoppingCartDto = TestUtil.generateShoppingCartDto(
                userService.saveDto(TestUtil.generateUserDto()).getId());

        shoppingCartDto = shoppingCartService.saveDto(shoppingCartDto);
        shoppingCartDto.setSelectedProducts(new HashSet<>());
        id = shoppingCartDto.getId();

        String expected = objectMapper.writeValueAsString(shoppingCartDto);

        mockMvc.perform(get(SHOPPING_CART_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    @Transactional
    void should_delete_shoppingCart_by_id() throws Exception {
        long id = shoppingCartService.saveDto(TestUtil.generateShoppingCartDto(
                userService.saveDto(TestUtil.generateUserDto()).getId())).getId();

        mockMvc.perform(delete(SHOPPING_CART_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(get(SHOPPING_CART_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_use_user_assigned_id_in_database_for_shopping_cart() throws Exception {
        ShoppingCartDto shoppingCartDto = TestUtil.generateShoppingCartDto(
                userService.saveDto(TestUtil.generateUserDto()).getId());

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