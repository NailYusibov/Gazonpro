package com.gitlab.controller;

import com.gitlab.controllers.api.rest.OrderRestApi;
import com.gitlab.dto.OrderDto;
import com.gitlab.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Slf4j
@Validated
@RestController
@SecurityRequirement(name = "bearerAuth")
public class OrderRestController implements OrderRestApi {

    private final OrderService orderService;

    public OrderRestController(OrderService orderService) {
        this.orderService = orderService.clone();
    }

    public ResponseEntity<List<OrderDto>> getPage(Integer page, Integer size) {
        var orderPage = orderService.getPageDto(page, size);
        if (orderPage == null || orderPage.getContent().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(orderPage.getContent());
    }

    @Override
    public ResponseEntity<OrderDto> get(Long id) {
        return orderService.findByIdDto(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<OrderDto> create(OrderDto orderDto) {
        Optional<OrderDto> savedOrderDto = orderService.saveDto(orderDto);
        return savedOrderDto.map(dto -> ResponseEntity.status(HttpStatus.CREATED)
                .body(dto)).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @Override
    public ResponseEntity<OrderDto> update(Long id, OrderDto orderDto) {
        Optional<OrderDto> updateOrderDto = orderService.updateDto(id, orderDto);
        return updateOrderDto
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
