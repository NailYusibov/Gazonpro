package com.gitlab.client;

import com.gitlab.dto.PaymentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "gazonPaymentService", url = "${client_PaymentClient.url}")
public interface PaymentClient {

    @PostMapping("/api/payment/make-payment")
    ResponseEntity<PaymentDto> makePayment(@RequestBody PaymentDto paymentDto);
}
