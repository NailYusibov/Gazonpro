package com.gitlab.controller;

import com.gitlab.TestUtil;
import com.gitlab.dto.StoreDto;
import com.gitlab.dto.UserDto;
import com.gitlab.service.StoreService;
import com.gitlab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

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

public class StoreRestControllerIT extends AbstractIntegrationTest {

    private static final String STORE_URN = "/api/store";
    private static final String STORE_URI = URL + STORE_URN;

    @Autowired
    private StoreService storeService;
    @Autowired
    private UserService userService;

    @Test
    @Transactional(readOnly = true)
    void should_get_all_stores() throws Exception {

        var response = storeService.getPage(null, null);
        var expected = objectMapper.writeValueAsString(response.getContent());

        mockMvc.perform(get(STORE_URI))
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

        var response = storeService.getPage(page, size);
        assertFalse(response.getContent().isEmpty());

        var expected = objectMapper.writeValueAsString(response.getContent());

        mockMvc.perform(get(STORE_URI + parameters))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_get_page_with_incorrect_parameters() throws Exception {
        int page = 0;
        int size = -2;
        String parameters = "?page=" + page + "&size=" + size;

        mockMvc.perform(get(STORE_URI + parameters))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void should_get_page_without_content() throws Exception {
        int page = 10;
        int size = 100;
        String parameters = "?page=" + page + "&size=" + size;

        mockMvc.perform(get(STORE_URI + parameters))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void should_get_store_by_id() throws Exception {
        StoreDto storeDto = TestUtil.generateStoreDto(
                userService.saveDto(TestUtil.generateUserDto()).getId(),
                TestUtil.generateUserDtos().stream()
                        .map(userDto -> userService.saveDto(userDto).getId())
                        .collect(Collectors.toSet()));

        long id = storeService.save(storeDto).get().getId();

        String expected = objectMapper.writeValueAsString(
                storeService
                        .findById(id)
                        .orElse(null));

        mockMvc.perform(get(STORE_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_return_not_found_when_get_store_by_non_existent_id() throws Exception {
        long id = -10L;

        mockMvc.perform(get(STORE_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_create_store() throws Exception {
        StoreDto storeDto = TestUtil.generateStoreDto(
                userService.saveDto(TestUtil.generateUserDto()).getId(),
                TestUtil.generateUserDtos().stream()
                        .map(userDto -> userService.saveDto(userDto).getId())
                        .collect(Collectors.toSet()));

        String jsonProductDto = objectMapper.writeValueAsString(storeDto);

        mockMvc.perform(post(STORE_URI)
                        .content(jsonProductDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void should_update_store_by_id() throws Exception {
        long id;
        int numberOfEntitiesExpected;

        StoreDto storeDto = TestUtil.generateStoreDto(
                userService.saveDto(TestUtil.generateUserDto()).getId(),
                TestUtil.generateUserDtos().stream()
                        .map(userDto -> userService.saveDto(userDto).getId())
                        .collect(Collectors.toSet()));

        storeDto = storeService.save(storeDto).get();

        id = storeDto.getId();
        numberOfEntitiesExpected = storeService.findAll().size();

        storeDto.setOwnerId(userService.saveDto(TestUtil.generateUserDto()).getId());

        String jsonProductDto = objectMapper.writeValueAsString(storeDto);

        mockMvc.perform(patch(STORE_URI + "/{id}", id)
                        .content(jsonProductDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(jsonProductDto))
                .andExpect(result -> assertThat(storeService.findAll().size(), equalTo(numberOfEntitiesExpected)));
    }

    @Test
    void should_return_not_found_when_update_store_by_non_existent_id() throws Exception {
        long id = 9999L;

        StoreDto storeDto = TestUtil.generateStoreDto(
                userService.saveDto(TestUtil.generateUserDto()).getId(),
                TestUtil.generateUserDtos().stream()
                        .peek(userDto -> userService.saveDto(userDto))
                        .map(UserDto::getId)
                        .collect(Collectors.toSet()));

        String jsonProductDto = objectMapper.writeValueAsString(storeDto);

        mockMvc.perform(patch(STORE_URI + "/{id}", id)
                        .content(jsonProductDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_delete_store_by_id() throws Exception {
        StoreDto storeDto = TestUtil.generateStoreDto(
                userService.saveDto(TestUtil.generateUserDto()).getId(),
                TestUtil.generateUserDtos().stream()
                        .map(userDto -> userService.saveDto(userDto).getId())
                        .collect(Collectors.toSet()));

        long id = storeService.save(storeDto).get().getId();

        mockMvc.perform(delete(STORE_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(get(STORE_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());

    }

    @Test
    void should_use_user_assigned_id_in_database_for_store() throws Exception {
        StoreDto storeDto = TestUtil.generateStoreDto(
                userService.saveDto(TestUtil.generateUserDto()).getId(),
                TestUtil.generateUserDtos().stream()
                        .map(userDto -> userService.saveDto(userDto).getId())
                        .collect(Collectors.toSet()));

        storeDto.setId(9999L);

        String jsonStoreDto = objectMapper.writeValueAsString(storeDto);

        MockHttpServletResponse response = mockMvc.perform(post(STORE_URI)
                        .content(jsonStoreDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        StoreDto createdStoreDto = objectMapper.readValue(response.getContentAsString(), StoreDto.class);
        Assertions.assertNotEquals(storeDto.getId(), createdStoreDto.getId());
    }
}