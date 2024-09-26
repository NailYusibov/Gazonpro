package com.gitlab.client;

import com.gitlab.dto.PaymentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "gazonPaymentService", url = "${client_PaymentClient.url}")
public interface PaymentClient {

    @PostMapping("/api/payment")
    ResponseEntity<PaymentDto> makePayment(@RequestBody PaymentDto paymentDto);

    @GetMapping("/api/payment")
    ResponseEntity<List<PaymentDto>> getPaymentsPage(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size);

    @GetMapping("/api/payment/{id}")
    ResponseEntity<Optional<PaymentDto>> getPaymentById(@PathVariable Long id);

    @PutMapping("/api/payment/{id}")
    ResponseEntity<PaymentDto> updatePayment(@PathVariable Long id, @RequestBody PaymentDto paymentDto);
}
