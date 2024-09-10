package com.gitlab.mapper;

import com.gitlab.controller.AbstractIntegrationTest;
import com.gitlab.dto.BankCardDto;
import com.gitlab.dto.PaymentDto;
import com.gitlab.enums.PaymentStatus;
import com.gitlab.model.BankCard;
import com.gitlab.model.Order;
import com.gitlab.model.Payment;
import com.gitlab.model.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaymentMapperTest extends AbstractIntegrationTest {

    @Autowired
    private PaymentMapper mapper;

    @Autowired
    private  BankCardMapper bankCardMapper;

    @Test
    void should_map_payment_to_Dto() {

        Payment payment = getPayment(1L);

        PaymentDto actualResult = mapper.toDto(payment);

        assertNotNull(actualResult);
        assertEquals(payment.getBankCard(), bankCardMapper.toEntity(actualResult.getBankCardDto()));
        assertEquals(payment.getPaymentStatus(), actualResult.getPaymentStatus());
        assertEquals(payment.getCreateDateTime(), actualResult.getCreateDateTime());
        assertEquals(payment.getOrder().getId(), actualResult.getOrderId());
        assertEquals(payment.getSum(), actualResult.getSum());
    }

    @Test
    void should_map_paymentDto_to_Entity() {
        PaymentDto paymentDto = getPaymentDto(1L);

        Payment actualResult = mapper.toEntity(paymentDto);

        assertNotNull(actualResult);
        assertEquals(paymentDto.getPaymentStatus(), actualResult.getPaymentStatus());
        assertEquals(paymentDto.getCreateDateTime(), actualResult.getCreateDateTime());
        assertEquals(paymentDto.getOrderId(), actualResult.getOrder().getId());
        assertEquals(paymentDto.getSum(), actualResult.getSum());
    }

    @Test
    void should_map_paymentList_to_DtoList() {
        List<Payment> paymentList = List.of(getPayment(1L), getPayment(2L), getPayment(3L));

        List<PaymentDto> paymentDtoList = mapper.toDtoList(paymentList);

        assertNotNull(paymentDtoList);
        assertEquals(paymentList.size(), paymentList.size());
        for (int i = 0; i < paymentDtoList.size(); i++) {
            PaymentDto entity = paymentDtoList.get(i);
            Payment dto = paymentList.get(i);
            assertEquals(dto.getBankCard(), bankCardMapper.toEntity(entity.getBankCardDto()));
            assertEquals(dto.getPaymentStatus(), entity.getPaymentStatus());
            assertEquals(dto.getCreateDateTime(), entity.getCreateDateTime());
            assertEquals(dto.getOrder().getId(), entity.getOrderId());
            assertEquals(dto.getSum(), entity.getSum());
        }
    }

    @Test
    void should_map_paymentDtoList_to_EntityList() {
        List<PaymentDto> paymentDtoList = List.of(getPaymentDto(1L), getPaymentDto(2L), getPaymentDto(3L));

        List<Payment> paymentList = mapper.toEntityList(paymentDtoList);

        assertNotNull(paymentList);
        assertEquals(paymentList.size(), paymentList.size());
        for (int i = 0; i < paymentList.size(); i++) {
            PaymentDto entity = paymentDtoList.get(i);
            Payment dto = paymentList.get(i);
            assertEquals(dto.getPaymentStatus(), entity.getPaymentStatus());
            assertEquals(dto.getCreateDateTime(), entity.getCreateDateTime());
            assertEquals(dto.getOrder().getId(), entity.getOrderId());
            assertEquals(dto.getSum(), entity.getSum());
        }
    }

    @NotNull
    private Payment getPayment(Long id) {
        Payment payment = new Payment();
        BankCard bankCard = new BankCard();
        bankCard.setId(id);
        bankCard.setCardNumber("482807843969662" + id);
        bankCard.setDueDate(LocalDate.parse("2029-09-2" + id));
        bankCard.setSecurityCode(354);

        payment.setBankCard(bankCard);
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setCreateDateTime(LocalDateTime.now());
        Order order = new Order();
        order.setId(id);
        payment.setOrder(order);
        payment.setSum(new BigDecimal(500));
        User user = new User();
        user.setId(id);
        return payment;
    }

    @NotNull
    private PaymentDto getPaymentDto(Long id) {
        PaymentDto paymentDto = new PaymentDto();

        BankCardDto bankCardDto = new BankCardDto();
        bankCardDto.setId(id);
        bankCardDto.setCardNumber("482807843969662" + id);
        bankCardDto.setDueDate(LocalDate.parse("2029-09-2" + id));
        bankCardDto.setSecurityCode(354);

        paymentDto.setBankCardDto(bankCardDto);
        paymentDto.setPaymentStatus(PaymentStatus.PAID);
        paymentDto.setCreateDateTime(LocalDateTime.now());
        paymentDto.setOrderId(id);
        paymentDto.setSum(new BigDecimal(500));
        return paymentDto;
    }
}