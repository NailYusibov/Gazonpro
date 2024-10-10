package com.gitlab.service;

import com.gitlab.client.PaymentClient;
import com.gitlab.dto.OrderDto;
import com.gitlab.dto.PaymentDto;
import com.gitlab.enums.OrderStatus;
import com.gitlab.enums.PaymentStatus;
import com.gitlab.exception.handler.NoResponseException;
import com.gitlab.model.Order;
import com.gitlab.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService implements Cloneable {

    private final OrderService orderService;
    private final UserService userService;
    private final PaymentClient paymentClient;

    public List<PaymentDto> findAllDto() {
        log.info("Fetching all payments");
        ResponseEntity<List<PaymentDto>> responseEntity = paymentClient.getPaymentsPage(null, null);
        List<PaymentDto> paymentDtoList = responseEntity.getBody();
        if (paymentDtoList == null || paymentDtoList.isEmpty()) {
            log.warn("No payments found");
            return Collections.emptyList();
        }
        log.info("Found {} payments", paymentDtoList.size());
        return paymentDtoList;
    }

    public Optional<PaymentDto> findPaymentByIdDto(Long id) {
        log.info("Fetching payment with id: {}", id);
        ResponseEntity<Optional<PaymentDto>> responseEntity = paymentClient.getPaymentById(id);
        Optional<PaymentDto> paymentDto = responseEntity.getBody();
        if (paymentDto.isEmpty()) {
            log.warn("Payment with id: {} not found", id);
        } else {
            log.info("Payment with id: {} found", id);
        }
        return paymentDto != null ? paymentDto : Optional.empty();
    }

    public List<PaymentDto> getPageDto(Integer page, Integer size) {
        log.info("Fetching payments page - page: {}, size: {}", page, size);
        if (page == null || size == null) {
            log.info("Fetching all payments without pagination");
            return findAllDto()
                    .stream()
                    .sorted(Comparator.comparing(PaymentDto::getId))
                    .toList();
        }

        ResponseEntity<List<PaymentDto>> responseEntity = paymentClient.getPaymentsPage(page, size);
        if (responseEntity == null) {
            log.warn("No response from payment service for page: {}, size: {}", page, size);
            return List.of();
        }

        List<PaymentDto> paymentDtos = responseEntity.getBody();
        if (paymentDtos == null || paymentDtos.isEmpty()) {
            log.warn("No payments found for page: {}, size: {}", page, size);
            return List.of();
        }

        log.info("Returning {} payments for page: {}, size: {}", paymentDtos.size(), page, size);
        return paymentDtos.stream().sorted(Comparator.comparing(PaymentDto::getId)).toList();
    }

    public PaymentDto saveDto(PaymentDto paymentDto) {
        log.info("Saving payment: {}", paymentDto);
        ResponseEntity<PaymentDto> paymentDtoResponseEntity = paymentClient.makePayment(paymentDto);
        PaymentDto paymentDtoResponse = paymentDtoResponseEntity.getBody();

        if (paymentDtoResponse == null) {
            log.error("No response from payment microservice when saving payment");
            throw new NoResponseException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not get response from gazon-payment microservice");
        }

        log.info("Payment saved with status: {}", paymentDtoResponse.getPaymentStatus());

        if (paymentDtoResponse.getPaymentStatus().equals(PaymentStatus.PAID)) {
            log.info("Payment successful, updating order status to PAID for orderId: {}", paymentDtoResponse.getOrderId());
            OrderDto orderDto = orderService.findByIdDto(paymentDtoResponse.getOrderId())
                    .orElseThrow(() -> new EntityNotFoundException("Order with id %s was not found".formatted(paymentDtoResponse.getOrderId())));

            orderDto.setOrderStatus(OrderStatus.PAID);
            orderService.saveDto(orderDto);
            log.info("Order status updated to PAID for orderId: {}", paymentDtoResponse.getOrderId());
        }

        return paymentDtoResponse;
    }

    public Optional<PaymentDto> updateDto(Long id, PaymentDto paymentDto) {
        log.info("Updating payment with id: {}", id);
        ResponseEntity<Optional<PaymentDto>> responseEntity = paymentClient.getPaymentById(id);
        Optional<PaymentDto> optionalSavedPayment = responseEntity.getBody();

        if (optionalSavedPayment.isEmpty()) {
            log.warn("Payment with id: {} not found for update", id);
            return Optional.empty();
        }

        log.info("Found payment with id: {} for update", id);
        Optional<User> paymentUser = userService.findUserById(userService.getAuthenticatedUser().getId());
        if (paymentUser.isEmpty()) {
            log.error("User not found for updating payment with id: {}", id);
            throw new EntityNotFoundException("User not found");
        }

        Optional<Order> paymentOrder = orderService.findById(paymentDto.getOrderId());
        if (paymentOrder.isEmpty()) {
            log.error("Order not found for updating payment with id: {}", paymentDto.getOrderId());
            throw new EntityNotFoundException("Order not found");
        }

        log.info("Updating payment with id: {}", id);
        ResponseEntity<PaymentDto> updatedPaymentResponse = paymentClient.updatePayment(id, paymentDto);
        PaymentDto updatedPaymentDto = updatedPaymentResponse.getBody();
        log.info("Payment with id: {} successfully updated", id);
        return Optional.ofNullable(updatedPaymentDto);
    }

    @Override
    public PaymentService clone() {
        try {
            return (PaymentService) super.clone();
        } catch (CloneNotSupportedException e) {
            log.error("Error cloning PaymentService", e);
            throw new AssertionError();
        }
    }
}
