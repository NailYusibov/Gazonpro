package com.gitlab.dto;

import com.gitlab.enums.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentDtoTest extends AbstractDtoTest {

    @Test
    void test_valid_payment() {
        var paymentDto = generatePaymentDto();
        assertTrue(validator.validate(paymentDto).isEmpty());
    }

    @Test
    void test_invalid_bank_card(){
        var paymentDto = generatePaymentDto();
        paymentDto.setBankCardDto(null);

        assertFalse(validator.validate(paymentDto).isEmpty());
    }

    @Test
    void test_invalid_payment_status(){
        var paymentDto = generatePaymentDto();
        paymentDto.setPaymentStatus(null);

        assertFalse(validator.validate(paymentDto).isEmpty());
    }

    @Test
    void test_invalid_create_date_time(){
        var paymentDto = generatePaymentDto();
        paymentDto.setCreateDateTime(null);

        assertFalse(validator.validate(paymentDto).isEmpty());
    }

    @Test
    void test_invalid_orderId(){
        var paymentDto = generatePaymentDto();
        paymentDto.setOrderId(null);

        assertFalse(validator.validate(paymentDto).isEmpty());
    }

    @Test
    void test_invalid_sum(){
        var paymentDto = generatePaymentDto();
        paymentDto.setSum(null);

        assertFalse(validator.validate(paymentDto).isEmpty());
    }

    @Test
    void test_invalid_shouldSaveCard() {
        var paymentDto = generatePaymentDto();
        assertTrue(validator.validate(paymentDto).isEmpty());
        paymentDto.setShouldSaveCard(true);
        assertThat(paymentDto.isShouldSaveCard()).isTrue();
        paymentDto.setShouldSaveCard(false);
        assertThat(paymentDto.isShouldSaveCard()).isFalse();
    }

    private PaymentDto generatePaymentDto() {
        PaymentDto paymentDto = new PaymentDto();
        BankCardDto bankCardDto = new BankCardDto();
        bankCardDto.setId(1L);

        paymentDto.setBankCardDto(bankCardDto);
        paymentDto.setPaymentStatus(PaymentStatus.PAID);
        paymentDto.setCreateDateTime(LocalDateTime.now());

        paymentDto.setOrderId(1L);
        paymentDto.setSum(new BigDecimal(500));
        return paymentDto;
    }
}
