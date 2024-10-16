package com.gitlab.controller;

import com.gitlab.TestUtil;
import com.gitlab.dto.PersonalAddressDto;
import com.gitlab.mapper.PersonalAddressMapper;
import com.gitlab.service.PersonalAddressService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.hamcrest.CoreMatchers.equalTo;
import static org.testcontainers.shaded.org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
class PersonalAddressRestControllerIT extends AbstractIntegrationTest {

    private static final String URN = "/api/personal-address";
    private static final String URI = URL + URN;
    @Autowired
    private PersonalAddressService personalAddressService;
    @Autowired
    private PersonalAddressMapper personalAddressMapper;

    @Test
    @Transactional(readOnly = true)
    @WithMockUser(username = "admin1",roles ="ADMIN")
    void should_get_all_personalAddresses() throws Exception {

        var response = personalAddressService.getPage(null, null);
        var expected = objectMapper.writeValueAsString(personalAddressMapper.toDtoList(response.getContent()));

        mockMvc.perform(get(URI))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    @WithMockUser(username = "admin1",roles ="ADMIN")
    @Transactional(readOnly = true)
    void should_get_page() throws Exception {
        int page = 0;
        int size = 2;
        String parameters = "?page=" + page + "&size=" + size;

        var response = personalAddressService.getPage(page, size);
        assertFalse(response.getContent().isEmpty());

        var expected = objectMapper.writeValueAsString(personalAddressMapper.toDtoList(response.getContent()));

        mockMvc.perform(get(URI + parameters))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    @WithMockUser(username = "admin1",roles ="ADMIN")
    void should_get_page_with_incorrect_parameters() throws Exception {
        int page = 0;
        int size = -2;
        String parameters = "?page=" + page + "&size=" + size;

        mockMvc.perform(get(URI + parameters))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin1",roles ="ADMIN")
    void should_get_page_without_content() throws Exception {
        int page = 10;
        int size = 100;
        String parameters = "?page=" + page + "&size=" + size;

        mockMvc.perform(get(URI + parameters))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin1",roles ="ADMIN")
    void should_get_personalAddress_by_id() throws Exception {
        PersonalAddressDto personalAddressDto = TestUtil.generatePersonalAddressDto();
        PersonalAddressDto savedPersonalAddress = personalAddressService.saveDto(personalAddressDto);

        String expected = objectMapper.writeValueAsString(
                personalAddressMapper.toDto(
                        personalAddressService
                                .findById(savedPersonalAddress.getId())
                                .orElse(null))
        );

        mockMvc.perform(get(URI + "/{id}", savedPersonalAddress.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    @WithMockUser(username = "admin1",roles ="ADMIN")
    void should_return_not_found_when_get_personalAddress_by_non_existent_id() throws Exception {
        long id = 9999L;
        mockMvc.perform(get(URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin1",roles ="ADMIN")
    void should_create_personalAddress() throws Exception {
        PersonalAddressDto personalAddressDto = TestUtil.generatePersonalAddressDto();
        String jsonPersonalAddressDto = objectMapper.writeValueAsString(personalAddressDto);

        mockMvc.perform(post(URI)
                        .content(jsonPersonalAddressDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "admin1",roles ="ADMIN")
    void should_update_personalAddress_by_id() throws Exception {
        PersonalAddressDto personalAddressDto = TestUtil.generatePersonalAddressDto();
        PersonalAddressDto savedPersonalAddressDto = personalAddressService.saveDto(personalAddressDto);

        int numberOfEntitiesExpected = personalAddressService.findAll().size();

        PersonalAddressDto uptadedPersonalAddressDto = TestUtil.generatePersonalAddressDto();
        uptadedPersonalAddressDto.setId(savedPersonalAddressDto.getId());

        String jsonPersonalAddressDto = objectMapper.writeValueAsString(uptadedPersonalAddressDto);
        String expected = objectMapper.writeValueAsString(uptadedPersonalAddressDto);

        mockMvc.perform(patch(URI + "/{id}", savedPersonalAddressDto.getId())

                        .content(jsonPersonalAddressDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected))
                .andExpect(result -> assertThat(personalAddressService.findAll().size(),
                        equalTo(numberOfEntitiesExpected)));
    }

    @Test
    @WithMockUser(username = "admin1",roles ="ADMIN")
    void should_return_not_found_when_update_personalAddress_by_non_existent_id() throws Exception {
        long id = 9999L;
        PersonalAddressDto personalAddressDto = TestUtil.generatePersonalAddressDto();
        String jsonPersonalAddressDto = objectMapper.writeValueAsString(personalAddressDto);

        mockMvc.perform(patch(URI + "/{id}", id)
                        .content(jsonPersonalAddressDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin1",roles ="ADMIN")
    void should_delete_personalAddress_by_id() throws Exception {
        PersonalAddressDto personalAddressDto = TestUtil.generatePersonalAddressDto();
        PersonalAddressDto savedPersonalAddress = personalAddressService.saveDto(personalAddressDto);

        mockMvc.perform(delete(URI + "/{id}", savedPersonalAddress.getId()))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(get(URI + "/{id}", savedPersonalAddress.getId()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin1", roles = "ADMIN")
    void should_use_user_assigned_id_in_database_for_personal_address() throws Exception {
        PersonalAddressDto personalAddressDto = TestUtil.generatePersonalAddressDto();
        personalAddressDto.setId(9999L);

        String jsonPersonalAddressDto = objectMapper.writeValueAsString(personalAddressDto);
        MockHttpServletResponse response = mockMvc.perform(post(URI)
                        .content(jsonPersonalAddressDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        PersonalAddressDto createdPersonalAddressDto = objectMapper.readValue(response.getContentAsString(), PersonalAddressDto.class);
        Assertions.assertNotEquals(personalAddressDto.getId(), createdPersonalAddressDto.getId());
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER_LEAD")
    void should_not_get_all_personalAddress_authorized_user_with_403() throws Exception {
        mockMvc.perform(get(URI))
                .andDo(print())
                .andExpect(status().is(403));
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER_LEAD")
    void should_not_get_personalAddress_by_id_with_404() throws Exception {
        long id = personalAddressService.saveDto(TestUtil.generatePersonalAddressDto()).getId();
        mockMvc.perform(get(URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    void should_not_update_personalAddress_by_id_with_404() throws Exception {
        PersonalAddressDto personalAddressDto = TestUtil.generatePersonalAddressDto();
        PersonalAddressDto savedPersonalAddressDto = personalAddressService.saveDto(personalAddressDto);

        savedPersonalAddressDto.setDoorCode("1");
        savedPersonalAddressDto.setFloor("8");

        String jsonTestBankCardDto = objectMapper.writeValueAsString(savedPersonalAddressDto);

        mockMvc.perform(patch(URI + "/{id}", savedPersonalAddressDto.getId())
                    .content(jsonTestBankCardDto)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    void should_create_personalAddress_with_authorized_user() throws Exception {
        PersonalAddressDto personalAddressDto = TestUtil.generatePersonalAddressDto();
        String jsonPersonalAddressDto = objectMapper.writeValueAsString(personalAddressDto);

        mockMvc.perform(post(URI)
                        .content(jsonPersonalAddressDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER_LEAD")
    void should_not_delete_personalAddress_by_id_with_404() throws Exception {
        long id = personalAddressService.saveDto(TestUtil.generatePersonalAddressDto()).getId();
        mockMvc.perform(delete(URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isForbidden());
    }


}