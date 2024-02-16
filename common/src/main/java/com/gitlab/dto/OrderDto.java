package com.gitlab.dto;


import com.gitlab.enums.OrderStatus;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.ReadOnlyProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Setter
public class OrderDto {

    @ReadOnlyProperty
    private Long id;

    @NotNull(message = "Shipping address should not be null. Please provide a valid shipping address")
    private ShippingAddressDto shippingAddressDto;

    @NotNull(message = "Shipping date should not be null. Please provide a valid shipping date")
    private LocalDate shippingDate;

    @NotEmpty(message = "Order code should be empty. Please provide a valid order code")
    @Size(min = 1, max = 20)
    private String orderCode;

    @NotNull(message = "Datetime of creation should not be null. Please provide a valid datetime")
    private LocalDateTime createDateTime;

    @NotNull(message = "Sum should not be null. Please provide a valid sum")
    private BigDecimal sum;

    @NotNull(message = "Discount should not be null. Please provide a valid discount")
    private BigDecimal discount;

    @NotNull(message = "Bag counter should not be null. Please provide a valid bag counter")
    private Byte bagCounter;

    @NotNull(message = "User ID should not be null. Please provide a valid user ID")
    private Long userId;

    private Set<SelectedProductDto> selectedProducts;

    private OrderStatus orderStatus;
}