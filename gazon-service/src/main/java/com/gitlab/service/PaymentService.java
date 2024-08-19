package com.gitlab.service;

import com.gitlab.client.PaymentClient;
import com.gitlab.dto.OrderDto;
import com.gitlab.dto.PaymentDto;
import com.gitlab.enums.OrderStatus;
import com.gitlab.enums.PaymentStatus;
import com.gitlab.exception.handler.NoResponseException;
import com.gitlab.mapper.PaymentMapper;
import com.gitlab.model.BankCard;
import com.gitlab.model.Order;
import com.gitlab.model.Payment;
import com.gitlab.model.User;
import com.gitlab.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BankCardService bankCardService;
    private final PaymentMapper paymentMapper;
    private final OrderService orderService;
    private final UserService userService;
    private final PaymentClient paymentClient;

    public List<Payment> findAll() {
        return paymentRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public List<PaymentDto> findAllDto() {
        List<Payment> payments = findAll();
        return payments.stream()
                .map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }

    public Optional<PaymentDto> findByIdDto(Long id) {
        return paymentRepository.findById(id)
                .map(paymentMapper::toDto);
    }

    public Page<Payment> getPage(Integer page, Integer size) {
        if (page == null || size == null) {
            var payments = findAll();
            if (payments.isEmpty()) {
                return Page.empty();
            }
            return new PageImpl<>(payments);
        }
        if (page < 0 || size < 1) {
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        return paymentRepository.findAll(pageRequest);
    }

    public Page<PaymentDto> getPageDto(Integer page, Integer size) {

        if (page == null || size == null) {
            var payments = findAllDto();
            if (payments.isEmpty()) {
                return Page.empty();
            }
            return new PageImpl<>(payments);
        }
        if (page < 0 || size < 1) {
            return Page.empty();
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        Page<Payment> paymentPage = paymentRepository.findAll(pageRequest);
        return paymentPage.map(paymentMapper::toDto);
    }


    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    public PaymentDto saveDto(PaymentDto paymentDto) {
        paymentDto.setPaymentStatus(PaymentStatus.NOT_PAID);

        Payment payment = paymentMapper.toEntity(paymentDto);
        Payment savedPayment = paymentRepository.save(payment);

        if (userService.getAuthenticatedUser().getBankCardsSet()
                .contains(payment.getBankCard())) {
            userService.getAuthenticatedUser().getBankCardsSet().add(payment.getBankCard());
            paymentDto.setShouldSaveCard(true);
        }

        // send request to gazon-payment
        ResponseEntity<PaymentDto> paymentDtoResponseEntity = paymentClient.makePayment(paymentMapper.toDto(savedPayment));
        PaymentDto paymentDtoResponse = paymentDtoResponseEntity.getBody();

        if (paymentDtoResponse == null) {
            throw new NoResponseException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not get response from gazon-payment microservice");
        }

        if (paymentDtoResponse.getPaymentStatus().equals(PaymentStatus.PAID)) {
            OrderDto orderDto = orderService.findByIdDto(paymentDtoResponse.getOrderId())
                    .orElseThrow(() -> new EntityNotFoundException("Order with id %s was not found".formatted(paymentDtoResponse.getOrderId())));

            orderDto.setOrderStatus(OrderStatus.PAID);
            orderService.saveDto(orderDto);

            Payment savedDtoResponsePayment = paymentRepository.save(paymentMapper.toEntity(paymentDtoResponse));
            paymentDtoResponse.setId(savedDtoResponsePayment.getId());
        }

        return paymentDtoResponse;
    }

    public Optional<PaymentDto> updateDto(Long id, PaymentDto paymentDto) {
        Optional<Payment> optionalSavedPayment = findById(id);

        if (optionalSavedPayment.isEmpty()) {
            return Optional.empty();
        }

        Optional<BankCard> paymentBankCard = bankCardService.findById(paymentDto.getBankCardDto().getId());
        if (paymentBankCard.isEmpty()) {
            throw new EntityNotFoundException("Банковская карта не найдена");
        }

        Optional<User> paymentUser = userService.findUserById(userService.getAuthenticatedUser().getId());
        if (paymentUser.isEmpty()) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        Optional<Order> paymentOrder = orderService.findById(paymentDto.getOrderId());
        if (paymentOrder.isEmpty()) {
            throw new EntityNotFoundException("Заказ не найден");
        }

        Payment savedPayment = paymentMapper.toUpdateEntity(optionalSavedPayment.get(), paymentDto, paymentBankCard.get(),
                                                            paymentOrder.get(), paymentUser.get());

        savedPayment = paymentRepository.save(savedPayment);

        return Optional.of(paymentMapper.toDto(savedPayment));
    }

    public Optional<Payment> delete(Long id) {
        Optional<Payment> optionalSavedPayment = findById(id);

        if (optionalSavedPayment.isPresent()) {
            paymentRepository.deleteById(id);
        }

        return optionalSavedPayment;
    }
}