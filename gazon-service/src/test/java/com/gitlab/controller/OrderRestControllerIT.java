package com.gitlab.controller;

import com.gitlab.TestUtil;
import com.gitlab.dto.OrderDto;
import com.gitlab.enums.OrderStatus;
import com.gitlab.mapper.OrderMapper;
import com.gitlab.service.OrderService;
import com.gitlab.service.PersonalAddressService;
import com.gitlab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.hamcrest.CoreMatchers.equalTo;
import static org.testcontainers.shaded.org.hamcrest.MatcherAssert.assertThat;

public class OrderRestControllerIT extends AbstractIntegrationTest {

    private static final String ORDER_URN = "/api/order";
    private static final String ORDER_URI = URL + ORDER_URN;

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private PersonalAddressService personalAddressService;

    @Test
    @Transactional
    void should_get_all_orders() throws Exception {
        orderService.saveDto(
                TestUtil.generateOrderDto(
                        userService.saveDto(TestUtil.generateUserDto()).getId(),
                        personalAddressService.saveDto(TestUtil.generatePersonalAddressDto())));

        var response = orderService.getPage(null, null);
        var expected = objectMapper.writeValueAsString(orderMapper.toDtoList(response.getContent()));

        mockMvc.perform(get(ORDER_URI))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    @Transactional
    void should_get_page() throws Exception {
        orderService.saveDto(
                TestUtil.generateOrderDto(
                        userService.saveDto(TestUtil.generateUserDto()).getId(),
                        personalAddressService.saveDto(TestUtil.generatePersonalAddressDto())));

        int page = 0;
        int size = 2;
        String parameters = "?page=" + page + "&size=" + size;

        var response = orderService.getPage(page, size);
        assertFalse(response.getContent().isEmpty());

        var expected = objectMapper.writeValueAsString(orderMapper.toDtoList(response.getContent()));

        mockMvc.perform(get(ORDER_URI + parameters))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_get_page_with_incorrect_parameters() throws Exception {
        int page = 0;
        int size = -2;
        String parameters = "?page=" + page + "&size=" + size;

        mockMvc.perform(get(ORDER_URI + parameters))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void should_get_page_without_content() throws Exception {
        int page = 10;
        int size = 100;
        String parameters = "?page=" + page + "&size=" + size;

        mockMvc.perform(get(ORDER_URI + parameters))
                .andDo(print())
                .andExpect(status().isNoContent());
    }


    @Test
    void should_get_order_by_id() throws Exception {
        long id = orderService.saveDto(
                        TestUtil.generateOrderDto(
                                userService.saveDto(TestUtil.generateUserDto()).getId(),
                                personalAddressService.saveDto(TestUtil.generatePersonalAddressDto())))
                .getId();

        var orderDto = orderService.findByIdDto(id).orElse(null);
        String expected = objectMapper.writeValueAsString(orderDto);

        mockMvc.perform(get(ORDER_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_create_order() throws Exception {
        OrderDto orderDto = TestUtil.generateOrderDto(
                userService.saveDto(TestUtil.generateUserDto()).getId(),
                personalAddressService.saveDto(TestUtil.generatePersonalAddressDto()));

        String jsonOrderDto = objectMapper.writeValueAsString(orderDto);

        mockMvc.perform(post(ORDER_URI)
                        .content(jsonOrderDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @Transactional
    void check_null_update() throws Exception {
        OrderDto testOrderDto = orderService.saveDto(
                TestUtil.generateOrderDto(
                        userService.saveDto(TestUtil.generateUserDto()).getId(),
                        personalAddressService.saveDto(TestUtil.generatePersonalAddressDto())));

        int numberOfEntitiesExpected = orderService.findAll().size();

        String checkJsonOrderDto = objectMapper.writeValueAsString(testOrderDto);

        testOrderDto.setShippingAddressDto(null);
        testOrderDto.setOrderCode(null);
        testOrderDto.setShippingDate(null);
        testOrderDto.setCreateDateTime(null);
        testOrderDto.setSum(null);
        testOrderDto.setDiscount(null);
        testOrderDto.setBagCounter(null);
        testOrderDto.setOrderStatus(null);

        String jsonOrderDto = objectMapper.writeValueAsString(testOrderDto);
        mockMvc.perform(patch(ORDER_URI + "/{id}", testOrderDto.getId())
                        .content(jsonOrderDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().json(checkJsonOrderDto))
                .andExpect(result -> assertThat(orderService.findAll().size(),
                        equalTo(numberOfEntitiesExpected)));
    }

    @Test
    @Transactional
    void should_update_order_by_id() throws Exception {
        long id;
        OrderDto orderDto = TestUtil.generateOrderDto(userService.saveDto(TestUtil.generateUserDto()).getId(),
                personalAddressService.saveDto(TestUtil.generatePersonalAddressDto()));
        orderDto = orderService.saveDto(orderDto);

        id = orderDto.getId();
        int numberOfEntitiesExpected = orderService.findAll().size();

        orderDto.setOrderStatus(OrderStatus.IN_PROGRESS);
        orderDto.setCreateDateTime(LocalDateTime.now());

        String jsonOrderDto = objectMapper.writeValueAsString(orderDto);
        String expected = objectMapper.writeValueAsString(orderDto);

        mockMvc.perform(patch(ORDER_URI + "/{id}", id)
                        .content(jsonOrderDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected))
                .andExpect(result -> assertThat(orderService.findAll().size(),
                        equalTo(numberOfEntitiesExpected)));
    }

    @Test
    void should_delete_order_by_id() throws Exception {
        OrderDto orderDto =
                TestUtil.generateOrderDto(userService.saveDto(TestUtil.generateUserDto()).getId(),
                        personalAddressService.saveDto(TestUtil.generatePersonalAddressDto()));

        orderDto = orderService.saveDto(orderDto);
        long id = orderDto.getId();
        mockMvc.perform(delete(ORDER_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(get(ORDER_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_not_found_when_get_order_by_non_existent_id() throws Exception {
        long id = 100L;
        mockMvc.perform(get(ORDER_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_not_found_when_update_order_by_non_existent_id() throws Exception {
        long id = 100L;
        OrderDto orderDto = TestUtil.generateOrderDto(userService.saveDto(TestUtil.generateUserDto()).getId(),
                personalAddressService.saveDto(TestUtil.generatePersonalAddressDto()));
        String jsonOrderDto = objectMapper.writeValueAsString(orderDto);

        mockMvc.perform(patch(ORDER_URI + "/{id}", id)
                        .content(jsonOrderDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_use_user_assigned_id_in_database_for_order() throws Exception {
        OrderDto orderDto = TestUtil.generateOrderDto(userService.saveDto(TestUtil.generateUserDto()).getId(),
                personalAddressService.saveDto(TestUtil.generatePersonalAddressDto()));

        orderDto.setId(9999L);
        String jsonOrderDto = objectMapper.writeValueAsString(orderDto);

        MockHttpServletResponse response = mockMvc.perform(post(ORDER_URI)
                        .content(jsonOrderDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        OrderDto createdOrderDto = objectMapper.readValue(response.getContentAsString(), OrderDto.class);
        Assertions.assertNotEquals(orderDto.getId(), createdOrderDto.getId());
    }
}
