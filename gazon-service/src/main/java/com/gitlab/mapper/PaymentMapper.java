package com.gitlab.mapper;

import com.gitlab.dto.PaymentDto;
import com.gitlab.model.BankCard;
import com.gitlab.model.Order;
import com.gitlab.model.Payment;
import com.gitlab.model.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentMapper {

    @Mapping(source = "bankCard", target = "bankCardDto")
    @Mapping(source = "order.id", target = "orderId")
    PaymentDto toDto(Payment payment);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "bankCardDto", target = "bankCard")
    @Mapping(source = "orderId", target = "order.id")
    Payment toEntity(PaymentDto paymentDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "paymentDto.sum", target = "sum")
    @Mapping(source = "paymentDto.createDateTime", target = "createDateTime")
    @Mapping(source = "order", target = "order")
    Payment toUpdateEntity(@MappingTarget Payment payment,
                                           PaymentDto paymentDto,
                                           BankCard bankCard,
                                           Order order,
                                           User user);


    List<PaymentDto> toDtoList(List<Payment> paymentList);

    List<Payment> toEntityList(List<PaymentDto> paymentDtoList);
}