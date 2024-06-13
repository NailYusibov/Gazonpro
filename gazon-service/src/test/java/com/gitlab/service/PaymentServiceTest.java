package com.gitlab.service;

import com.gitlab.model.Payment;
import com.gitlab.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @InjectMocks
    private PaymentService paymentService;

    @Test
    void should_find_all_payments() {
        List<Payment> expectedResult = generatePayments();
        when(paymentRepository.findAll((Sort) any())).thenReturn(generatePayments());

        List<Payment> actualResult = paymentService.findAll();

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void should_find_payment_by_id() {
        long id = 1L;
        Payment expectedResult = generatePayment();
        when(paymentRepository.findById(id)).thenReturn(Optional.of(expectedResult));

        Optional<Payment> actualResult = paymentService.findById(id);

        assertEquals(expectedResult, actualResult.orElse(null));
    }

    @Test
    void should_save_payment() {
        Payment expectedResult = generatePayment();
        when(paymentRepository.save(expectedResult)).thenReturn(expectedResult);

        Payment actualResult = paymentService.save(expectedResult);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void should_delete_payment() {
        long id = 1L;
        when(paymentRepository.findById(id)).thenReturn(Optional.of(generatePayment()));

        paymentService.delete(id);

        verify(paymentRepository).deleteById(id);
    }

    @Test
    void should_not_delete_payment_when_entity_not_found() {
        long id = 1L;
        when(paymentRepository.findById(id)).thenReturn(Optional.empty());

        paymentService.delete(id);

        verify(paymentRepository, never()).deleteById(anyLong());
    }

    private List<Payment> generatePayments() {
        return List.of(
                new Payment(1L),
                new Payment(2L),
                new Payment(3L),
                new Payment(4L),
                new Payment(5L),
                new Payment(6L)
        );
    }

    private Payment generatePayment() {
        return new Payment(1L);
    }
}