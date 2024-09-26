package com.gitlab.controller;

import com.gitlab.service.PaymentService;
import com.gitlab.dto.PaymentDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class PaymentRestController {

    private final PaymentService paymentService;

    public PaymentRestController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/api/payment")
    ResponseEntity<PaymentDto> makePayment(@RequestBody PaymentDto paymentDto) throws InterruptedException {
        PaymentDto savedPaymentDto = paymentService.savePayment(paymentDto);
        Thread.sleep(200);
        return ResponseEntity.ok(savedPaymentDto);
    }

    @GetMapping("/api/payment/{id}")
    ResponseEntity<PaymentDto> getPaymentById(@PathVariable Long id) {
        Optional<PaymentDto> paymentDto = paymentService.findPaymentById(id);
        return paymentDto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/api/payment")
    public ResponseEntity<List<PaymentDto>> getPaymentsPage(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        List<PaymentDto> paymentPage = paymentService.getPageDto(page, size);
        if (paymentPage.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(paymentPage);
    }
    @PutMapping("/api/payment/{id}")
    ResponseEntity<PaymentDto> updatePayment(@PathVariable Long id, @RequestBody PaymentDto paymentDto) throws InterruptedException {
        PaymentDto updatedPaymentDto = paymentService.updatePayment(id, paymentDto);
        Thread.sleep(200);
        return ResponseEntity.ok(updatedPaymentDto);
    }
}
