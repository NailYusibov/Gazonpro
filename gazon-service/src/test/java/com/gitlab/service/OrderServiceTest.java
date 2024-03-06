package com.gitlab.service;

import com.gitlab.dto.OrderDto;
import com.gitlab.enums.EntityStatus;
import com.gitlab.mapper.*;
import com.gitlab.model.Order;
import com.gitlab.repository.OrderRepository;
import com.gitlab.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;
    @Mock
    private OrderMapper orderMapper;

    @Test
    void should_find_all_orders() {
        List<Order> expectedOrders = generateOrders();
        when(orderRepository.findAll()).thenReturn(generateOrders());
        List<Order> actualOrders = orderService.findAll();
        assertEquals(expectedOrders, actualOrders);
    }

    @Test
    void should_find_order_by_id() {
        Long id = 1L;
        Order expectedOrder = generateOrder();
        when(orderRepository.findById(id)).thenReturn(Optional.of(generateOrder()));
        Optional<Order> actualOrder = orderService.findById(id);
        assertEquals(expectedOrder, actualOrder.orElse(null));

    }

    @Test
    void should_save_order() {
        Long id = 1L;
        Order expectedResult = generateOrder();
        OrderDto orderDto = generateOrderDto();
        orderDto.setId(id);

        when(orderMapper.toDto(any(Order.class))).thenReturn(orderDto);
        when(orderMapper.toEntity(orderDto)).thenReturn(expectedResult);

        when(orderRepository.save(expectedResult)).thenReturn(expectedResult);

        Order actualResult = orderMapper.toEntity(orderService.saveDto(orderMapper.toDto(expectedResult)));

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    void should_update_order() {
        long id = 1L;

        OrderDto orderDtoToUpdate = generateOrderDto();
        orderDtoToUpdate.setOrderCode("updatesOrder");

        Order orderBeforeUpdate = new Order(id, "unmodifiedCode");
        Order updatedOrder = new Order(id, "updatesOrder");

        when(orderMapper.toDto(any(Order.class))).thenReturn(orderDtoToUpdate);
        when(orderMapper.toEntity(orderDtoToUpdate)).thenReturn(updatedOrder);

        when(orderRepository.findById(id)).thenReturn(Optional.of(orderBeforeUpdate));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        Optional<Order> actualResult = Optional.of(orderMapper
                .toEntity(orderService.updateDto(id, orderDtoToUpdate).get()));

        assertEquals(updatedOrder, actualResult.orElse(null));

    }

    @Test
    void should_not_update_order_when_entity_not_found() {
        long id = 1L;
        OrderDto orderDtoToUpdate = new OrderDto();
        orderDtoToUpdate.setOrderCode("updatesOrder");

        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        Optional<OrderDto> actualDtoResult = orderService.updateDto(id, orderDtoToUpdate);

        verify(orderRepository, never()).save(any());
        assertNull(actualDtoResult.orElse(null));
    }

    @Test
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    void should_not_updated_orderCode_field_if_null() {
        long id = 1L;
        OrderDto orderDtoToUpdate = new OrderDto();
        orderDtoToUpdate.setOrderCode(null);

        Order orderBeforeUpdate = new Order(id, "unmodifiedCode");

        when(orderMapper.toDto(any(Order.class))).thenReturn(orderDtoToUpdate);
        when(orderMapper.toEntity(orderDtoToUpdate)).thenReturn(orderBeforeUpdate);

        when(orderRepository.findById(id)).thenReturn(Optional.of(orderBeforeUpdate));
        when(orderRepository.save(orderBeforeUpdate)).thenReturn(orderBeforeUpdate);

        Optional<Order> actualResult = Optional.of( orderMapper.toEntity(orderService.updateDto(id, orderDtoToUpdate).get()));

        verify(orderRepository).save(orderBeforeUpdate);
        assertEquals(orderBeforeUpdate, actualResult.orElse(null));
        assertEquals("unmodifiedCode", orderBeforeUpdate.getOrderCode());
    }

    @Test
    void should_delete_order() {

        Order order = generateOrder();
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        Order delete = orderService.delete(order.getId()).orElseGet(null);
        assertEquals(EntityStatus.DELETED, delete.getEntityStatus());
    }

    @Test
    void should_not_delete_order_when_entity_not_found() {
        long id = 1L;
        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        orderService.delete(id);

        verify(orderRepository, never()).deleteById(anyLong());
    }


    private List<Order> generateOrders() {
        return List.of(
                new Order(1L),
                new Order(2L),
                new Order(3L),
                new Order(4L),
                new Order(5L),
                new Order(6L)
        );
    }

    private Order generateOrder() {
        Order order = new Order();
        order.setEntityStatus(EntityStatus.ACTIVE);
        order.setId(1L);
        return order;
    }

    private OrderDto generateOrderDto() {

        return new OrderDto();

    }

    @Test
    void updateOverdueOrders_NoOverdueOrders() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<Order> notPaidOrders = new ArrayList<>();

        Order order1 = new Order();
        order1.setCreateDateTime(currentTime.plusMinutes(2));
        notPaidOrders.add(order1);

        Order order2 = new Order();
        order2.setCreateDateTime(currentTime.minusMinutes(2));
        notPaidOrders.add(order2);

        when(orderRepository.findOrdersWithNotPaidStatus()).thenReturn(notPaidOrders);
        orderService.updateOverdueOrders();

        verify(orderRepository, never()).save(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateOverdueOrders_WithOverdueOrders() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<Order> notPaidOrders = new ArrayList<>();

        Order order1 = new Order();
        order1.setCreateDateTime(currentTime.minusHours(2));
        notPaidOrders.add(order1);

        Order order2 = new Order();
        order2.setCreateDateTime(currentTime.minusDays(1));
        notPaidOrders.add(order2);

        when(orderRepository.findOrdersWithNotPaidStatus()).thenReturn(notPaidOrders);
        orderService.updateOverdueOrders();

        verify(orderRepository, times(2)).save(any());
        verify(productRepository, never()).save(any());
    }

}
