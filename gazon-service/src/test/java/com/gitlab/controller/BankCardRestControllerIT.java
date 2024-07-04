package com.gitlab.controller;

import com.gitlab.TestUtil;
import com.gitlab.dto.BankCardDto;
import com.gitlab.mapper.BankCardMapper;
import com.gitlab.service.BankCardService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.hamcrest.CoreMatchers.equalTo;
import static org.testcontainers.shaded.org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@WithMockUser(roles="ADMIN")
class BankCardRestControllerIT extends AbstractIntegrationTest {

    private static final String BANK_CARD_URN = "/api/bank-card";
    private static final String BANK_CARD_URI = URL + BANK_CARD_URN;
    @Autowired
    private BankCardService bankCardService;
    @Autowired
    private BankCardMapper bankCardMapper;

    @Test
    @Transactional
    void should_get_all_bankCards() throws Exception {
        bankCardService.saveDto(TestUtil.generateBankCardDto());
        var response = bankCardService.getPage(null, null);
        var expected = objectMapper.writeValueAsString(bankCardMapper.toDtoList(response.getContent()));

        mockMvc.perform(get(BANK_CARD_URI))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    @Transactional
    void should_get_page() throws Exception {
        BankCardDto testBankCardDto = bankCardService.saveDto(TestUtil.generateBankCardDto());
        int page = 0;
        int size = 2;
        String parameters = "?page=" + page + "&size=" + size;

        var response = bankCardService.getPage(page, size);
        Assertions.assertFalse(response.getContent().isEmpty());

        var expected = objectMapper.writeValueAsString(bankCardMapper.toDtoList(response.getContent()));

        mockMvc.perform(get(BANK_CARD_URI + parameters))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
        bankCardService.delete(testBankCardDto.getId());
    }

    @Test
    void should_get_page_with_incorrect_parameters() throws Exception {
        int page = 0;
        int size = -2;
        String parameters = "?page=" + page + "&size=" + size;

        mockMvc.perform(get(BANK_CARD_URI + parameters))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void should_get_page_without_content() throws Exception {
        int page = 10;
        int size = 100;
        String parameters = "?page=" + page + "&size=" + size;

        mockMvc.perform(get(BANK_CARD_URI + parameters))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void should_get_bankCard_by_id() throws Exception {
        long id = bankCardService.saveDto(TestUtil.generateBankCardDto()).getId();
        String expected = objectMapper.writeValueAsString(
                bankCardMapper.toDto(
                        bankCardService
                                .findById(id)
                                .orElse(null))
        );

        mockMvc.perform(get(BANK_CARD_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_return_not_found_when_get_bankCard_by_non_existent_id() throws Exception {
        long id = 9999L;
        mockMvc.perform(get(BANK_CARD_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_create_bankCard() throws Exception {
        BankCardDto testBankCardDto = TestUtil.generateBankCardDto();
        String jsonBankCardDto = objectMapper.writeValueAsString(testBankCardDto);

        mockMvc.perform(post(BANK_CARD_URI)
                        .content(jsonBankCardDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void check_null_update() throws Exception {
        BankCardDto testBankCardDto = bankCardService.saveDto(TestUtil.generateBankCardDto());
        int numberOfEntitiesExpected = bankCardService.findAll().size();

        testBankCardDto.setCardNumber(null);
        testBankCardDto.setSecurityCode(null);
        testBankCardDto.setDueDate(null);

        String jsonBankCardDto = objectMapper.writeValueAsString(testBankCardDto);
        mockMvc.perform(patch(BANK_CARD_URI + "/{id}", testBankCardDto.getId())
                        .content(jsonBankCardDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertThat(bankCardService.findAll().size(),
                        equalTo(numberOfEntitiesExpected)));
    }

    @Test
    void should_update_bankCard_by_id() throws Exception {
        BankCardDto testBankCardDto = bankCardService.saveDto(TestUtil.generateBankCardDto());
        int numberOfEntitiesExpected = bankCardService.findAll().size();


        testBankCardDto.setCardNumber("1234123412341234");
        testBankCardDto.setSecurityCode(6969);
        testBankCardDto.setDueDate(LocalDate.now());

        String jsonTestBankCardDto = objectMapper.writeValueAsString(testBankCardDto);

        mockMvc.perform(patch(BANK_CARD_URI + "/{id}", testBankCardDto.getId())
                        .content(jsonTestBankCardDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(jsonTestBankCardDto))
                .andExpect(result -> assertThat(bankCardService.findAll().size(),
                        equalTo(numberOfEntitiesExpected)));
    }

    @Test
    void should_return_not_found_when_update_bankCard_by_non_existent_id() throws Exception {
        long id = -10L;
        BankCardDto testBankCardDto = TestUtil.generateBankCardDto();
        String jsonBankCardDto = objectMapper.writeValueAsString(testBankCardDto);

        mockMvc.perform(patch(BANK_CARD_URI + "/{id}", id)
                        .content(jsonBankCardDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_delete_bankCard_by_id() throws Exception {
        BankCardDto testBankCardDto = bankCardService.saveDto(TestUtil.generateBankCardDto());
        long id = testBankCardDto.getId();
        mockMvc.perform(delete(BANK_CARD_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(get(BANK_CARD_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_use_user_assigned_id_in_database() throws Exception {
        BankCardDto bankCardDto = TestUtil.generateBankCardDto();
        bankCardDto.setId(9999L);

        String jsonBankCardDto = objectMapper.writeValueAsString(bankCardDto);
        MockHttpServletResponse response = mockMvc.perform(post(BANK_CARD_URI)
                        .content(jsonBankCardDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        BankCardDto createdBankCardDto = objectMapper.readValue(response.getContentAsString(), BankCardDto.class);
        Assertions.assertNotEquals(bankCardDto.getId(), createdBankCardDto.getId());
    }
}
