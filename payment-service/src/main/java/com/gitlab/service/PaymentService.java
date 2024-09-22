package com.gitlab.service;

import com.gitlab.dto.PaymentDto;
import com.gitlab.enums.PaymentStatus;
import com.gitlab.mapper.PaymentMapper;
import com.gitlab.model.Payment;
import com.gitlab.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;


    public PaymentDto savePayment(PaymentDto paymentDto) {
        Payment payment = paymentMapper.toEntity(paymentDto);
        payment.setPaymentStatus(PaymentStatus.NOT_PAID);
        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toDto(savedPayment);
    }
    public PaymentDto updatePayment(Long id, PaymentDto paymentDto) {
        if (paymentDto.getId() == null) {
            throw new IllegalArgumentException("Payment ID must not be null for update.");
        }
        // Проверка наличия платежа
        Payment existingPayment = paymentRepository.findById(paymentDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + paymentDto.getId()));
        // Преобразование DTO в сущность и обновление
        Payment payment = paymentMapper.toEntity(paymentDto);
        payment.setId(existingPayment.getId()); // Сохранение существующего ID
        Payment savedPayment = paymentRepository.save(payment);
        // Возвращаем DTO
        return paymentMapper.toDto(savedPayment);
    }
    public Optional<PaymentDto> findPaymentById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null.");
        }
        // Поиск платежа по ID
        return paymentRepository.findById(id)
                .map(paymentMapper::toDto);
    }

    public List<PaymentDto> getPageDto(Integer page, Integer size) {
        if (page == null || size == null) {
            return paymentRepository.findAll()
                    .stream()
                    .map(paymentMapper::toDto)
                    .toList();
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Payment> paymentPage = paymentRepository.findAll(pageable);
        return paymentPage.getContent()
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }
}
