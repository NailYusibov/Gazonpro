package com.gitlab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitlab.dto.*;
import com.gitlab.enums.Citizenship;
import com.gitlab.enums.Gender;
import com.gitlab.mapper.UserMapper;
import com.gitlab.model.User;
import com.gitlab.service.ShoppingCartService;
import com.gitlab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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

class UserRestControllerIT extends AbstractIntegrationTest {

    private static final String USER_URN = "/api/user";
    private static final String USER_URI = URL + USER_URN;

    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Test
    @Transactional(readOnly = true)
    void should_get_all_users() throws Exception {

        var response = userService.getPage(null, null);
        var expected = objectMapper.writeValueAsString(userMapper.toDtoList(response.getContent()));

        mockMvc.perform(get(USER_URI))
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

        var response = userService.getPage(page, size);
        assertFalse(response.getContent().isEmpty());

        var expected = objectMapper.writeValueAsString(userMapper.toDtoList(response.getContent()));

        mockMvc.perform(get(USER_URI + parameters))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_get_page_with_incorrect_parameters() throws Exception {
        int page = 0;
        int size = -2;
        String parameters = "?page=" + page + "&size=" + size;

        mockMvc.perform(get(USER_URI + parameters))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void should_get_page_without_content() throws Exception {
        int page = 10;
        int size = 100;
        String parameters = "?page=" + page + "&size=" + size;

        mockMvc.perform(get(USER_URI + parameters))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void should_get_user_by_id() throws Exception {
        long id = 1L;
        String expected = objectMapper.writeValueAsString(
                userService
                        .findById(id)
                        .orElse(null)
        );

        mockMvc.perform(get(USER_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }



    @Test
    void should_return_not_found_when_get_user_by_non_existent_id() throws Exception {
        long id = 100L;
        mockMvc.perform(get(USER_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_create_user() throws Exception {

        String jsonExampleDto = objectMapper.writeValueAsString(generateUser(6L));

        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setUserId(6L);

        mockMvc.perform(post(USER_URI)
                        .content(jsonExampleDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(x -> assertThat(shoppingCartService.saveDto(shoppingCartDto).getUserId(),
                        equalTo(shoppingCartDto.getUserId())))
                .andExpect(status().isCreated());
    }

    @Test
    void should_update_user_by_id() throws Exception {
        long id = 8L;
        int numberOfEntitiesExpected = userService.findAll().size();

        UserDto userDto = userService.findById(id).get();
        userDto.setRoles(Set.of("ROLE_ADMIN"));
        userDto.setPassportDto(new PassportDto(null,
                Citizenship.ARMENIA,
                "David",
                "Davidyan",
                null,
                LocalDate.now(), LocalDate.now(), "dsadsdsadas", "sdsdds", "fdffdf"));
        userDto.setBankCardDtos(Set.of(
                new BankCardDto(null, "123L22234", LocalDate.now(), 123)));

        String expected = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(patch(USER_URI + "/{id}", id)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(userService.findAll().size(), equalTo(numberOfEntitiesExpected)));
    }


    @Test
    void should_return_not_found_when_update_user_by_non_existent_id() throws Exception {
        long id = 100L;
        UserDto userDto = generateUser(5L);

        String jsonUserDto = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(patch(USER_URI + "/{id}", id)
                        .content(jsonUserDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_delete_user_by_id() throws Exception {
        User entity = userMapper.toEntity(generateUser(5L));
        User user = userService.save(entity);
        long id = userService.findById(user.getId()).get().getId();

        mockMvc.perform(delete(USER_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(get(USER_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());

    }

    private UserDto generateUser(Long id) {
        Set<String> roleSet = new HashSet<>();
        roleSet.add("ROLE_ADMIN");

        Set<BankCardDto> bankCardSet = new HashSet<>();
        bankCardSet.add(new BankCardDto(
                10L,
                "1111222233334444",
                LocalDate.now(),
                423
        ));

        Set<ShippingAddressDto> personalAddress = new HashSet<>();
        personalAddress.add(new PersonalAddressDto(
                10L,
                "address",
                "directions",
                "apartment",
                "floor",
                "entrance",
                "doorCode",
                "postCode"));

        PassportDto passportDto = new PassportDto(
                10L,
                Citizenship.RUSSIA,
                "user",
                "user",
                "patronym",
                LocalDate.now(),
                LocalDate.now(),
                "098765",
                "issuer",
                "issuerN");


        return new UserDto(
                10L,
                "mail" + id + "@mail.ru",
                "username" + id,
                "user",
                "answer",
                "question",
                "user",
                "user",
                LocalDate.now(),
                Gender.MALE,
                "89007777777",
                passportDto,
                personalAddress,
                bankCardSet,
                roleSet
        );
    }
    @Test
    void should_use_user_assigned_id_in_database_for_user() throws Exception {
        UserDto userDto = generateUser(null);
        userDto.setId(9999L);
        String jsonUserDto = objectMapper.writeValueAsString(userDto);

        MockHttpServletResponse response = mockMvc.perform(post(USER_URI)
                        .content(jsonUserDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        UserDto createdUserDto = objectMapper.readValue(response.getContentAsString(), UserDto.class);
        Assertions.assertNotEquals(userDto.getId(), createdUserDto.getId());
    }



}