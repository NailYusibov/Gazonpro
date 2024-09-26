package com.gitlab.service;

import com.gitlab.client.PaymentClient;
import com.gitlab.dto.OrderDto;
import com.gitlab.dto.PaymentDto;
import com.gitlab.enums.OrderStatus;
import com.gitlab.enums.PaymentStatus;
import com.gitlab.model.BankCard;
import com.gitlab.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    private UserService userService;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void should_find_all_payments() {
        List<PaymentDto> expectedResult = generatePaymentsDtos();
        ResponseEntity<List<PaymentDto>> responseEntity = new ResponseEntity<>(expectedResult, HttpStatus.OK);

        when(paymentClient.getPaymentsPage(null, null)).thenReturn(responseEntity);

        List<PaymentDto> actualResult = paymentService.findAllDto();

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void should_find_payment_by_id() {
        long id = 1L;
        PaymentDto expectedResult = generatePaymentDto();
        ResponseEntity<Optional<PaymentDto>> responseEntity = new ResponseEntity<>(Optional.of(expectedResult), HttpStatus.OK);

        when(paymentClient.getPaymentById(id)).thenReturn(responseEntity);

        Optional<PaymentDto> actualResult = paymentService.findPaymentByIdDto(id);

        assertEquals(expectedResult, actualResult.orElse(null));
    }

    @Test
    void should_save_payment() {
        PaymentDto expectedResult = generatePaymentDto();
        expectedResult.setPaymentStatus(PaymentStatus.NOT_PAID);
        when(paymentClient.makePayment(expectedResult)).thenReturn(new ResponseEntity<>(expectedResult, HttpStatus.OK));

        PaymentDto actualResult = paymentService.saveDto(expectedResult);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testSaveDto_Success() {
        User user = new User();
        user.setId(123L);
        Set<BankCard> userCards = new HashSet<>();
        userCards.add(new BankCard());
        user.setBankCardsSet(userCards);

        PaymentDto paymentDto = generatePaymentDto();
        paymentDto.setPaymentStatus(PaymentStatus.NOT_PAID);

        PaymentDto paymentDtoResponse = generatePaymentDto();
        paymentDtoResponse.setPaymentStatus(PaymentStatus.PAID);
        paymentDtoResponse.setOrderId(1L);

        ResponseEntity<PaymentDto> paymentDtoResponseEntity = new ResponseEntity<>(paymentDtoResponse, HttpStatus.OK);

        OrderDto orderDto = new OrderDto();
        orderDto.setOrderStatus(OrderStatus.NOT_PAID);

        when(paymentClient.makePayment(any(PaymentDto.class))).thenReturn(paymentDtoResponseEntity);
        when(orderService.findByIdDto(1L)).thenReturn(Optional.of(orderDto));

        PaymentDto result = paymentService.saveDto(paymentDto);

        assertEquals(PaymentStatus.PAID, result.getPaymentStatus());
        verify(orderService).saveDto(orderDto);
        assertEquals(OrderStatus.PAID, orderDto.getOrderStatus());
    }



    private List<PaymentDto> generatePaymentsDtos() {
        return List.of(
                new PaymentDto(),
                new PaymentDto(),
                new PaymentDto(),
                new PaymentDto(),
                new PaymentDto(),
                new PaymentDto()
        );
    }

    private PaymentDto generatePaymentDto() {
        return new PaymentDto();
    }
}