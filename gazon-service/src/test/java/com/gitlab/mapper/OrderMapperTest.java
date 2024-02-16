package com.gitlab.mapper;

import com.gitlab.controller.AbstractIntegrationTest;
import com.gitlab.dto.OrderDto;
import com.gitlab.dto.SelectedProductDto;
import com.gitlab.enums.OrderStatus;
import com.gitlab.model.Order;
import com.gitlab.model.ShippingAddress;
import com.gitlab.model.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class OrderMapperTest extends AbstractIntegrationTest {

    @Autowired
    private OrderMapper mapper;

    @Autowired
    private  ShippingAddressMapper shippingAddressMapper;


    @Test
    void should_map_order_to_Dto(){
        Order order = getOrder(1L);

        OrderDto actualResult = mapper.toDto(order);

        assertNotNull(actualResult);
        assertEquals(order.getOrderCode(), actualResult.getOrderCode());
        assertEquals(order.getShippingAddress(), shippingAddressMapper.toEntity(actualResult.getShippingAddressDto()));
        assertEquals(order.getShippingDate(), actualResult.getShippingDate());
        assertEquals(order.getCreateDateTime(), actualResult.getCreateDateTime());
        assertEquals(order.getSum(), actualResult.getSum());
        assertEquals(order.getDiscount(), actualResult.getDiscount());
        assertEquals(order.getBagCounter(), actualResult.getBagCounter());
        assertEquals(order.getUser().getId(), actualResult.getUserId());
        assertEquals(order.getOrderStatus(), actualResult.getOrderStatus());

    }

    @Test
    void should_map_dto_to_order(){
        OrderDto orderDto = getOrderDto(1L);

        Order actualResult = mapper.toEntity(orderDto);

        assertNotNull(actualResult);
        assertEquals(orderDto.getOrderCode(), actualResult.getOrderCode());
        assertEquals(orderDto.getShippingDate(), actualResult.getShippingDate());
        assertEquals(orderDto.getCreateDateTime(), actualResult.getCreateDateTime());
        assertEquals(orderDto.getSum(), actualResult.getSum());
        assertEquals(orderDto.getDiscount(), actualResult.getDiscount());
        assertEquals(orderDto.getBagCounter(), actualResult.getBagCounter());
        assertEquals(orderDto.getUserId(), actualResult.getUser().getId());
        assertEquals(orderDto.getOrderStatus(), actualResult.getOrderStatus());

    }

    @Test
    void should_map_orderList_to_DtoList() {
        List<Order> orderList = List.of(getOrder(1L), getOrder(2L), getOrder(3L));

        List<OrderDto> orderDtoList = mapper.toDtoList(orderList);

        assertNotNull(orderDtoList);
        assertEquals(orderList.size(), orderList.size());
        for (int i = 0; i < orderDtoList.size(); i++) {
            OrderDto dto = orderDtoList.get(i);
            Order entity = orderList.get(i);
            assertEquals(dto.getId(), entity.getId());
            assertEquals(entity.getOrderCode(), dto.getOrderCode());
            assertEquals(entity.getShippingAddress(), shippingAddressMapper.toEntity(dto.getShippingAddressDto()));
            assertEquals(entity.getShippingDate(), dto.getShippingDate());
            assertEquals(entity.getCreateDateTime(), dto.getCreateDateTime());
            assertEquals(entity.getSum(), dto.getSum());
            assertEquals(entity.getDiscount(), dto.getDiscount());
            assertEquals(entity.getBagCounter(), dto.getBagCounter());
            assertEquals(entity.getUser().getId(), dto.getUserId());
            assertEquals(entity.getOrderStatus(), dto.getOrderStatus());
        }
    }

    @Test
    void should_map_orderDtoList_to_EntityList() {
        List<OrderDto> orderDtoList = List.of(getOrderDto(1L), getOrderDto(2L), getOrderDto(3L));

        List<Order> orderList = mapper.toEntityList(orderDtoList);

        assertNotNull(orderList);
        assertEquals(orderList.size(), orderList.size());
        for (int i = 0; i < orderList.size(); i++) {
            OrderDto dto = orderDtoList.get(i);
            Order entity = orderList.get(i);
            assertEquals(dto.getId(), entity.getId());
            assertEquals(entity.getOrderCode(), dto.getOrderCode());
            assertEquals(entity.getShippingAddress(), shippingAddressMapper.toEntity(dto.getShippingAddressDto()));
            assertEquals(entity.getShippingDate(), dto.getShippingDate());
            assertEquals(entity.getCreateDateTime(), dto.getCreateDateTime());
            assertEquals(entity.getSum(), dto.getSum());
            assertEquals(entity.getDiscount(), dto.getDiscount());
            assertEquals(entity.getBagCounter(), dto.getBagCounter());
            assertEquals(entity.getUser().getId(), dto.getUserId());
            assertEquals(entity.getOrderStatus(), dto.getOrderStatus());
        }
    }

    @NotNull
    private Order getOrder(Long id) {
        Order order = new Order();

        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setId(id);
        shippingAddress.setAddress("address");
        shippingAddress.setDirections("direction");
        order.setShippingAddress(shippingAddress);
        order.setShippingDate(LocalDate.parse("2027-05-0" + id));
        order.setOrderCode("code" + id);
        order.setCreateDateTime(LocalDateTime.now());
        order.setSum(new BigDecimal(5));
        order.setDiscount(new BigDecimal(6));
        order.setBagCounter((byte) (5 + id));
        User user = new User();
        user.setId(id);
        order.setUser(user);
        order.setSelectedProducts(Set.of());
        order.setOrderStatus(OrderStatus.IN_PROGRESS);
        return order;
    }

    @NotNull
    private OrderDto getOrderDto(Long id) {
        OrderDto orderDto = new OrderDto();

        SelectedProductDto selectedProductDto = new SelectedProductDto();
        selectedProductDto.setProductId(id);
        selectedProductDto.setId(id);
        orderDto.setOrderCode("123" + id);
        orderDto.setUserId(id);
        orderDto.setShippingDate(LocalDate.parse("2027-05-01"));
        orderDto.setCreateDateTime(LocalDateTime.now());
        orderDto.setSum(new BigDecimal(5 + id));
        orderDto.setDiscount(new BigDecimal(6 + id));
        orderDto.setBagCounter((byte) (5 + id));
        orderDto.setOrderStatus(OrderStatus.ARRIVED);
        return orderDto;
    }
}
