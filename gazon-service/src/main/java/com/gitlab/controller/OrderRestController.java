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
        log.info("Received request to get page with page: {}, size: {}", page, size);
        var orderPage = orderService.getPageDto(page, size);
        if (orderPage == null || orderPage.getContent().isEmpty()) {
            log.info("No content found for page: {}, size: {}", page, size);
            return ResponseEntity.noContent().build();
        }
        log.info("Returning {} orders for page: {}, size: {}", orderPage.getContent().size(), page, size);
        return ResponseEntity.ok(orderPage.getContent());
    }

    @Override
    public ResponseEntity<OrderDto> get(Long id) {
        log.info("Received request to get order with id: {}", id);
        return orderService.findByIdDto(id)
                .map(orderDto -> {
                    log.info("Order found: {}", orderDto);
                    return ResponseEntity.ok(orderDto);
                })
                .orElseGet(() -> {
                    log.info("Order with id: {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Override
    public ResponseEntity<OrderDto> create(OrderDto orderDto) {
        log.info("Received request to create order: {}", orderDto);
        Optional<OrderDto> savedOrderDto = orderService.saveDto(orderDto);
        return savedOrderDto.map(dto -> {
            log.info("Order created successfully: {}", dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        }).orElseGet(() -> {
            log.warn("Failed to create order: {}", orderDto);
            return ResponseEntity.badRequest().build();
        });
    }

    @Override
    public ResponseEntity<OrderDto> update(Long id, OrderDto orderDto) {
        log.info("Received request to update order with id: {}, data: {}", id, orderDto);
        Optional<OrderDto> updateOrderDto = orderService.updateDto(id, orderDto);
        return updateOrderDto.map(dto -> {
            log.info("Order updated successfully: {}", dto);
            return ResponseEntity.ok(dto);
        }).orElseGet(() -> {
            log.warn("Order with id: {} not found", id);
            return ResponseEntity.notFound().build();
        });
    }
}
