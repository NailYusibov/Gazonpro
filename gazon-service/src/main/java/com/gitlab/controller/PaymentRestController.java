package com.gitlab.controller;

import com.gitlab.controllers.api.rest.PaymentRestApi;
import com.gitlab.dto.PaymentDto;
import com.gitlab.model.Payment;
import com.gitlab.service.PaymentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PaymentRestController implements PaymentRestApi {

    private final PaymentService paymentService;

    @Override
    public ResponseEntity<List<PaymentDto>> getPage(Integer page, Integer size) {
        var paymentPage = paymentService.getPageDto(page, size);
        if (paymentPage == null || paymentPage.getContent().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(paymentPage.getContent());
    }


    @Override
    public ResponseEntity<PaymentDto> get(Long id) {
        return paymentService.findByIdDto(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<PaymentDto> create(PaymentDto paymentDto) {
        PaymentDto savedPaymentDto = paymentService.saveDto(paymentDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedPaymentDto);
    }

    @Override
    public ResponseEntity<PaymentDto> update(Long id, PaymentDto paymentDto) {
        Optional<PaymentDto> updatePaymentDto = paymentService.updateDto(id, paymentDto);
        return updatePaymentDto
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<PaymentDto> delete(Long id) {
        Optional<Payment> payment = paymentService.delete(id);
        if (payment.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().build();
        }
    }
}