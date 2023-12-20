package com.gitlab.controller;

import com.gitlab.dto.StoreDto;
import com.gitlab.service.StoreService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StoreRestControllerIT extends AbstractIntegrationTest {

    private static final String STORE_URN = "/api/store";
    private static final String STORE_URI = URL + STORE_URN;

    @Autowired
    private StoreService storeService;

    @Test
    void should_get_all_store() throws Exception {
        String expected = objectMapper.writeValueAsString(
                new ArrayList<>(storeService
                        .findAllDto())
        );

        mockMvc.perform(get(STORE_URI))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }
    @Test
    @Transactional
    void should_get_store_by_id() throws Exception {
        long id = 1L;
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
        StoreDto storeDto = generateStore();
        long id = storeDto.getId();

        String jsonProductDto = objectMapper.writeValueAsString(storeDto);

        mockMvc.perform(post(STORE_URI)
                        .content(jsonProductDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        mockMvc.perform(delete(STORE_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(get(STORE_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_update_store_by_id() throws Exception {
        StoreDto storeDto = generateStore();
        String jsonProductDto = objectMapper.writeValueAsString(storeDto);

        mockMvc.perform(post(STORE_URI)
                        .content(jsonProductDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        long id = storeDto.getId() - 1;
        storeDto.setId(id);
        storeDto.setOwnerId(2L);
        String expected = objectMapper.writeValueAsString(storeDto);


        mockMvc.perform(patch(STORE_URI + "/{id}", id)
                        .content(expected)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));

        mockMvc.perform(delete(STORE_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(get(STORE_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_not_found_when_update_store_by_non_existent_id() throws Exception {
        long id = 100000L;
        StoreDto storeDto = generateStore();

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
        StoreDto storeDto = generateStore();
        String jsonProductDto = objectMapper.writeValueAsString(storeDto);

        mockMvc.perform(post(STORE_URI)
                        .content(jsonProductDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        long id = storeDto.getId() - 2;

        mockMvc.perform(delete(STORE_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(get(STORE_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());

    }

    private StoreDto generateStore() {
        StoreDto storeDto = new StoreDto();
        storeDto.setId(5L);
        storeDto.setOwnerId(1L);
        storeDto.setManagersId(new HashSet<>());

        return storeDto;
    }
}