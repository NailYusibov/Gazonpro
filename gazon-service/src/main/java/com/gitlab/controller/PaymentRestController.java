package com.gitlab.controller;

import com.gitlab.controllers.api.rest.PaymentRestApi;
import com.gitlab.dto.PaymentDto;
import com.gitlab.service.PaymentService;
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
public class PaymentRestController implements PaymentRestApi {

    private final PaymentService paymentService;

    public PaymentRestController(PaymentService paymentService) {
        this.paymentService = paymentService.clone();
    }

    @Override
    public ResponseEntity<List<PaymentDto>> getPage(Integer page, Integer size) {
        log.info("Request to get payment page - page: {}, size: {}", page, size);
        var paymentPage = paymentService.getPageDto(page, size);
        if (paymentPage == null || paymentPage.isEmpty()) {
            log.warn("No payments found for page: {}, size: {}", page, size);
            return ResponseEntity.noContent().build();
        }
        log.info("Returning {} payments for page: {}, size: {}", paymentPage.size(), page, size);
        return ResponseEntity.ok(paymentPage);
    }

    @Override
    public ResponseEntity<PaymentDto> get(Long id) {
        log.info("Request to get payment with id: {}", id);
        return paymentService.findPaymentByIdDto(id)
                .map(paymentDto -> {
                    log.info("Payment found with id: {}", id);
                    return ResponseEntity.ok(paymentDto);
                })
                .orElseGet(() -> {
                    log.warn("Payment not found with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Override
    public ResponseEntity<PaymentDto> create(PaymentDto paymentDto) {
        log.info("Request to create payment: {}", paymentDto);
        PaymentDto savedPaymentDto = paymentService.saveDto(paymentDto);
        log.info("Payment created with id: {}", savedPaymentDto.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedPaymentDto);
    }

    @Override
    public ResponseEntity<PaymentDto> update(Long id, PaymentDto paymentDto) {
        log.info("Request to update payment with id: {}", id);
        Optional<PaymentDto> updatePaymentDto = paymentService.updateDto(id, paymentDto);
        return updatePaymentDto
                .map(updatedPayment -> {
                    log.info("Payment updated with id: {}", id);
                    return ResponseEntity.ok(updatedPayment);
                })
                .orElseGet(() -> {
                    log.warn("Payment not found for update with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }
}
