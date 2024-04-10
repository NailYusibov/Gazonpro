package com.gitlab.controller;

import com.gitlab.dto.OrderDto;
import com.gitlab.dto.SelectedProductDto;
import com.gitlab.dto.ShippingAddressDto;
import com.gitlab.enums.OrderStatus;
import com.gitlab.mapper.OrderMapper;
import com.gitlab.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

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

    @Test
    @Transactional
    void should_get_all_orders() throws Exception {
        orderService.saveDto(generateOrderDto());
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
        orderService.saveDto(generateOrderDto());
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
        long id = orderService.saveDto(generateOrderDto()).getId();
        var orderDto = orderService.findByIdDto(id).orElse(null);
        String expected = objectMapper.writeValueAsString(orderDto);

        mockMvc.perform(get(ORDER_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_create_order() throws Exception {
        OrderDto orderDto = generateOrderDto();

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
        OrderDto testOrderDto = orderService.saveDto(generateOrderDto());
        int numberOfEntitiesExpected = orderService.findAll().size();

        String checkJsonOrderDto = objectMapper.writeValueAsString(orderService.findByIdDto(testOrderDto.getId()).orElse(null));

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
        OrderDto orderDto = orderService.saveDto(generateOrderDto());

        ShippingAddressDto shippingAddressDto = new ShippingAddressDto();
        shippingAddressDto.setId(1L);
        shippingAddressDto.setAddress("ffffffffffffff");
        shippingAddressDto.setDirections("fffffffffff");

        orderDto.setSelectedProducts(Set.of(new SelectedProductDto()));
        orderDto.setShippingAddressDto(shippingAddressDto);
        orderDto.setOrderCode("321");
        orderDto.setShippingDate(LocalDate.parse("2077-07-07"));
        orderDto.setCreateDateTime(LocalDateTime.of(2023, 1, 15, 22, 2));
        orderDto.setSum(new BigDecimal(7));
        orderDto.setDiscount(new BigDecimal(7));
        orderDto.setBagCounter((byte) 7);

        long id = orderDto.getId();
        int numberOfEntitiesExpected = orderService.findAll().size();
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
        OrderDto orderDto = orderService.saveDto(generateOrderDto());
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
        long id = 10L;
        mockMvc.perform(get(ORDER_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_not_found_when_update_order_by_non_existent_id() throws Exception {
        long id = 10L;
        OrderDto orderDto = generateOrderDto();
        String jsonOrderDto = objectMapper.writeValueAsString(orderDto);

        mockMvc.perform(patch(ORDER_URI + "/{id}", id)
                        .content(jsonOrderDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    private OrderDto generateOrderDto() {
        OrderDto orderDto = new OrderDto();
        ShippingAddressDto shippingAddressDto = new ShippingAddressDto();
        shippingAddressDto.setId(1L);
        shippingAddressDto.setAddress("dfdefdf");
        shippingAddressDto.setDirections("dgfgg");
        orderDto.setSelectedProducts(Set.of(new SelectedProductDto()));
        orderDto.setShippingAddressDto(shippingAddressDto);
        orderDto.setUserId(1L);
        orderDto.setOrderCode("123");
        orderDto.setShippingDate(LocalDate.parse("2027-05-01"));
        orderDto.setCreateDateTime(LocalDateTime.now());
        orderDto.setSum(new BigDecimal(5));
        orderDto.setDiscount(new BigDecimal(6));
        orderDto.setBagCounter((byte) 5);
        orderDto.setOrderStatus(OrderStatus.DONE);
        return orderDto;
    }

    @Test
    void should_use_user_assigned_id_in_database_for_order() throws Exception {
        OrderDto orderDto = generateOrderDto();
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
