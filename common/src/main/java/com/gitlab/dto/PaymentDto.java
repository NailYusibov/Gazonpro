package com.gitlab.dto;

import com.gitlab.enums.PaymentStatus;
import lombok.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.ReadOnlyProperty;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {

    @ReadOnlyProperty
    private Long id;

    @NotNull(message = "Bank card should not be null. Please provide a valid bank card")
    private Long bankCardDto;

    @NotNull(message = "Payment status should not be null. Please provide a valid payment status")
    private PaymentStatus paymentStatus;

    @NotNull(message = "Local date time of creation should not be null. Please provide a valid local date time")
    private LocalDateTime createDateTime;

    @Range(min = 1, max = 2147483333, message = "Order Id should be between 1 and 2147483333")
    @NotNull(message = "Order Id should not be null. Please provide a valid order Id")
    private Long orderId;

    @DecimalMin(value = "0.1", message = "Sum should be between 0.1 and 2147483333")
    @DecimalMax(value = "2147483333", message = "Payment sum should be between 0.1 and 2147483333")
    @NotNull(message = "Sum should not be null. Please provide a valid sum")
    private BigDecimal sum;

    private boolean shouldSaveCard;
}