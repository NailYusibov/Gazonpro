package com.gitlab.service;

import com.gitlab.client.PaymentClient;
import com.gitlab.dto.OrderDto;
import com.gitlab.dto.PaymentDto;
import com.gitlab.enums.OrderStatus;
import com.gitlab.enums.PaymentStatus;
import com.gitlab.mapper.PaymentMapper;
import com.gitlab.model.BankCard;
import com.gitlab.model.Payment;
import com.gitlab.model.User;
import com.gitlab.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserService userService;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private OrderService orderService;

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
    public void testSaveDto_Success() {
        User user = new User();
        user.setId(123L);
        Set<BankCard> userCards = new HashSet<>();
        userCards.add(generatePayment().getBankCard());
        user.setBankCardsSet(userCards);

        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentStatus(PaymentStatus.NOT_PAID);

        Payment payment = new Payment();

        Payment savedPayment = new Payment();
        PaymentDto savedPaymentDto = new PaymentDto();
        savedPaymentDto.setPaymentStatus(PaymentStatus.PAID);
        savedPaymentDto.setOrderId(1L);

        PaymentDto paymentDtoResponse = new PaymentDto();
        paymentDtoResponse.setPaymentStatus(PaymentStatus.PAID);
        paymentDtoResponse.setOrderId(1L);

        ResponseEntity<PaymentDto> paymentDtoResponseEntity = new ResponseEntity<>(paymentDtoResponse, HttpStatus.OK);

        OrderDto orderDto = new OrderDto();
        orderDto.setOrderStatus(OrderStatus.NOT_PAID);

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(paymentMapper.toEntity(paymentDto)).thenReturn(payment);
        when(paymentRepository.save(payment)).thenReturn(savedPayment);
        Payment savedDtoResponseEntity = new Payment();
        savedDtoResponseEntity.setId(1L);
        when(paymentRepository.save(paymentMapper.toEntity(paymentDtoResponse))).thenReturn(savedDtoResponseEntity);
        when(paymentMapper.toDto(savedPayment)).thenReturn(savedPaymentDto);
        when(paymentClient.makePayment(any(PaymentDto.class))).thenReturn(paymentDtoResponseEntity);
        when(orderService.findByIdDto(1L)).thenReturn(Optional.of(orderDto));

        PaymentDto result = paymentService.saveDto(paymentDto);

        assertEquals(PaymentStatus.PAID, result.getPaymentStatus());
        verify(orderService).saveDto(orderDto);
        assertEquals(OrderStatus.PAID, orderDto.getOrderStatus());
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