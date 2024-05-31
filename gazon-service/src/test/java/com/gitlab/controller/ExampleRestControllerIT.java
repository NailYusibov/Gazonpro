package com.gitlab.controller;

import com.gitlab.TestUtil;
import com.gitlab.dto.ExampleDto;
import com.gitlab.mapper.ExampleMapper;
import com.gitlab.service.ExampleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

class ExampleRestControllerIT extends AbstractIntegrationTest {

    private static final String EXAMPLE_URN = "/api/example";
    private static final String EXAMPLE_URI = URL + EXAMPLE_URN;
    @Autowired
    private ExampleService exampleService;
    @Autowired
    private ExampleMapper exampleMapper;

    @Test
    @Transactional(readOnly = true)
    void should_get_all_examples() throws Exception {
        String expected = objectMapper.writeValueAsString(
                new ArrayList<>(exampleService
                        .getPage(null, null)
                        .stream()
                        .map(exampleMapper::toDto)
                        .collect(Collectors.toList()))
        );

        mockMvc.perform(get(EXAMPLE_URI))
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

        var response = exampleService.getPage(page, size);
        assertFalse(response.getContent().isEmpty());

        var expected = objectMapper.writeValueAsString(response
                .map(e -> exampleMapper.toDto(e)).stream().toList());

        mockMvc.perform(get(EXAMPLE_URI + parameters))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_get_example_by_id() throws Exception {
        long id;

        ExampleDto exampleDto = TestUtil.generateExampleDto();
        id = exampleService.saveDto(exampleDto).getId();

        String expected = objectMapper.writeValueAsString(
                exampleMapper.toDto(
                        exampleService
                                .findById(id)
                                .orElse(null))
        );

        mockMvc.perform(get(EXAMPLE_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_return_not_found_when_get_example_by_non_existent_id() throws Exception {
        long id = 9999L;
        mockMvc.perform(get(EXAMPLE_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_create_example() throws Exception {
        ExampleDto exampleDto = TestUtil.generateExampleDto();

        String jsonExampleDto = objectMapper.writeValueAsString(exampleDto);

        mockMvc.perform(post(EXAMPLE_URI)
                        .content(jsonExampleDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void should_update_example_by_id() throws Exception {
        long id;
        int numberOfEntitiesExpected;

        ExampleDto exampleDto = TestUtil.generateExampleDto();
        id = exampleService.saveDto(exampleDto).getId();

        numberOfEntitiesExpected = exampleService.findAll().size();

        exampleDto.setExampleText("updatedText");
        String jsonExampleDto = objectMapper.writeValueAsString(exampleDto);
        exampleDto.setId(id);
        String expected = objectMapper.writeValueAsString(exampleDto);

        mockMvc.perform(patch(EXAMPLE_URI + "/{id}", id)
                        .content(jsonExampleDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected))
                .andExpect(result -> assertThat(exampleService.findAll().size(),
                        equalTo(numberOfEntitiesExpected)));
    }

    @Test
    void should_return_not_found_when_update_example_by_non_existent_id() throws Exception {
        long id = 9999L;
        ExampleDto exampleDto = TestUtil.generateExampleDto();
        exampleDto.setExampleText("updatedText");
        String jsonExampleDto = objectMapper.writeValueAsString(exampleDto);

        mockMvc.perform(patch(EXAMPLE_URI + "/{id}", id)
                        .content(jsonExampleDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_delete_example_by_id() throws Exception {
        long id;

        ExampleDto exampleDto = TestUtil.generateExampleDto();
        id = exampleService.saveDto(exampleDto).getId();

        mockMvc.perform(delete(EXAMPLE_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(get(EXAMPLE_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}