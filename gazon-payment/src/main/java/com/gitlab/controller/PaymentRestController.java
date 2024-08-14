package com.gitlab.controller;

import com.gitlab.dto.PaymentDto;
import com.gitlab.enums.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PaymentRestController {

    @PostMapping("/api/payment/make-payment")
    ResponseEntity<PaymentDto> makePayment(@RequestBody PaymentDto paymentDto) throws InterruptedException {
        log.info("gazon-payment got the payment");
        paymentDto.setPaymentStatus(PaymentStatus.PAID);
        Thread.sleep(200);
        return ResponseEntity.ok(paymentDto);
    }
}
