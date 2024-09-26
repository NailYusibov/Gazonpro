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
        ResponseEntity<List<PaymentDto>> responseEntity = paymentClient.getPaymentsPage(null, null);
        List<PaymentDto> paymentDtoList = responseEntity.getBody();
        if (paymentDtoList == null || paymentDtoList.isEmpty()) {
            return Collections.emptyList();
        }
        return paymentDtoList;
    }


    public Optional<PaymentDto> findPaymentByIdDto(Long id) {

        ResponseEntity<Optional<PaymentDto>> responseEntity = paymentClient.getPaymentById(id);

        Optional<PaymentDto> paymentDto = responseEntity.getBody();
        return paymentDto != null ? paymentDto : Optional.empty();
    }

    public List<PaymentDto> getPageDto(Integer page, Integer size) {
        if (page == null || size == null) {
            return findAllDto()
                    .stream()
                    .sorted(Comparator.comparing(PaymentDto::getId))
                    .toList();
        }

        ResponseEntity<List<PaymentDto>> responseEntity = paymentClient.getPaymentsPage(page, size);
        if (responseEntity == null) {
            return List.of();
        }

        List<PaymentDto> paymentDtos = responseEntity.getBody();
        if (paymentDtos == null || paymentDtos.isEmpty()) {
            return List.of();
        }

        return findAllDto()
                .stream()
                .sorted(Comparator.comparing(PaymentDto::getId))
                .toList();
    }

    public PaymentDto saveDto(PaymentDto paymentDto) {
        // Отправляем запрос на создание платежа в другой микросервис
        ResponseEntity<PaymentDto> paymentDtoResponseEntity = paymentClient.makePayment(paymentDto);
        PaymentDto paymentDtoResponse = paymentDtoResponseEntity.getBody();

        // Проверка на получение ответа от микросервиса
        if (paymentDtoResponse == null) {
            throw new NoResponseException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not get response from gazon-payment microservice");
        }

        // Если платеж проведен успешно, обновляем статус заказа
        if (paymentDtoResponse.getPaymentStatus().equals(PaymentStatus.PAID)) {
            OrderDto orderDto = orderService.findByIdDto(paymentDtoResponse.getOrderId())
                    .orElseThrow(() -> new EntityNotFoundException("Order with id %s was not found".formatted(paymentDtoResponse.getOrderId())));

            orderDto.setOrderStatus(OrderStatus.PAID);
            orderService.saveDto(orderDto);
        }

        return paymentDtoResponse;
    }

    public Optional<PaymentDto> updateDto(Long id, PaymentDto paymentDto) {
        ResponseEntity<Optional<PaymentDto>> responseEntity = paymentClient.getPaymentById(id);
        Optional<PaymentDto> optionalSavedPayment = responseEntity.getBody();

        if (optionalSavedPayment.isEmpty()) {
            return Optional.empty();
        }

        Optional<User> paymentUser = userService.findUserById(userService.getAuthenticatedUser().getId());
        if (paymentUser.isEmpty()) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        Optional<Order> paymentOrder = orderService.findById(paymentDto.getOrderId());
        if (paymentOrder.isEmpty()) {
            throw new EntityNotFoundException("Заказ не найден");
        }

        ResponseEntity<PaymentDto> updatedPaymentResponse = paymentClient.updatePayment(id, paymentDto);
        PaymentDto updatedPaymentDto = updatedPaymentResponse.getBody();

        return Optional.ofNullable(updatedPaymentDto);
    }

    @Override
    public PaymentService clone() {
        try {
            return (PaymentService) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}