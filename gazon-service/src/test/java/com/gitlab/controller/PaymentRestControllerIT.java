package com.gitlab.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.gitlab.dto.BankCardDto;
import com.gitlab.dto.PaymentDto;
import com.gitlab.enums.PaymentStatus;
import com.gitlab.mapper.PaymentMapper;
import com.gitlab.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentRestControllerIT extends AbstractIntegrationTest {
    private static final String PAYMENT_URN = "/api/payment";
    private static final String PAYMENT_URI = URL + PAYMENT_URN;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentMapper paymentMapper;

    @Test
    @Transactional(readOnly = true)
    void should_get_all_payments() throws Exception {

        var response = paymentService.getPage(null, null);
        var expected = objectMapper.writeValueAsString(paymentMapper.toDtoList(response.getContent()));

        mockMvc.perform(get(PAYMENT_URI))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));

    }

    @Test
    void should_get_page_with_incorrect_parameters() throws Exception {
        int page = 0;
        int size = -2;
        String parameters = "?page=" + page + "&size=" + size;

        mockMvc.perform(get(PAYMENT_URI + parameters))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void should_get_page_without_content() throws Exception {
        int page = 10;
        int size = 100;
        String parameters = "?page=" + page + "&size=" + size;

        mockMvc.perform(get(PAYMENT_URI + parameters))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void should_get_payment_by_id() throws Exception {
        long id = 1L;
        String expected = objectMapper.writeValueAsString(
                paymentMapper.toDto(
                        paymentService
                                .findById(id)
                                .orElse(null))
        );

        mockMvc.perform(get(PAYMENT_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_return_not_found_when_get_payment_by_non_existent_id() throws Exception {
        long id = 10L;
        mockMvc.perform(get(PAYMENT_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_create_payment() throws Exception {
        PaymentDto paymentDto = generatePaymentDto();

        String jsonPaymentDto = objectMapper.writeValueAsString(paymentDto);

        mockMvc.perform(post(PAYMENT_URI)
                        .content(jsonPaymentDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void should_update_payment_by_id() throws Exception {
        PaymentDto paymentDto = generatePaymentDto();

        String jsonPaymentDto = objectMapper.writeValueAsString(paymentDto);

        //создаем переменную хранящую response, получаем id сохраненной сущности и используем его для
        //дальнейшей очистки БД от созданной сущности, чтобы избавиться от сайд-эффектов и хардкода
        ResultActions resultActions = mockMvc.perform(post(PAYMENT_URI)
                        .content(jsonPaymentDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        JsonNode createdEntity = objectMapper.readTree(contentAsString);
        long id = createdEntity.get("id").asLong();

        paymentDto.setId(id);
        paymentDto.setSum(new BigDecimal(2000));

        jsonPaymentDto = objectMapper.writeValueAsString(paymentDto);
        String expected = objectMapper.writeValueAsString(paymentDto);

        mockMvc.perform(patch(PAYMENT_URI + "/{id}", id)
                        .content(jsonPaymentDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));

        mockMvc.perform(delete(PAYMENT_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void should_return_not_found_when_update_payment_by_non_existent_id() throws Exception {
        long id = 9000L;
        PaymentDto paymentDto = generatePaymentDto();

        String jsonPaymentDto = objectMapper.writeValueAsString(paymentDto);

        mockMvc.perform(patch(PAYMENT_URI + "/{id}", id)
                        .content(jsonPaymentDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_delete_payment_by_id() throws Exception {
        long id = 2L;
        mockMvc.perform(delete(PAYMENT_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(get(PAYMENT_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private PaymentDto generatePaymentDto() {

        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setId(1L);
        BankCardDto bankCardDto = new BankCardDto();
        bankCardDto.setId(1L);
        bankCardDto.setCardNumber("4929078434696627");
        bankCardDto.setDueDate(LocalDate.parse("2027-05-01"));
        bankCardDto.setSecurityCode(775);
        paymentDto.setBankCardDto(bankCardDto);
        paymentDto.setPaymentStatus(PaymentStatus.PAID);
        paymentDto.setCreateDateTime(LocalDateTime.now());
        paymentDto.setOrderId(1L);
        paymentDto.setSum(new BigDecimal(500));
        paymentDto.setUserId(2L);

        return paymentDto;
    }
}