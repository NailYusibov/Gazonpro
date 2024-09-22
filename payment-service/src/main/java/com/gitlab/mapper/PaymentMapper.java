package com.gitlab.mapper;

import com.gitlab.dto.PaymentDto;
import com.gitlab.model.Payment;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentMapper {

    @Mapping(source = "bankCardId", target = "bankCardDto")
    @Mapping(source = "orderId", target = "orderId")
    PaymentDto toDto(Payment payment);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "bankCardDto", target = "bankCardId")
    @Mapping(source = "orderId", target = "orderId")
    Payment toEntity(PaymentDto paymentDto);
}