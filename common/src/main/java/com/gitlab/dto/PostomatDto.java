package com.gitlab.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.ReadOnlyProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@Data
@Setter
public class PostomatDto extends ShippingAddressDto {

    @ReadOnlyProperty
    @Schema(name = "id", example = "1", required = true)
    private Long id;

    @Size(min = 1, max = 255, message = "Length of address should be between 1 and 255 characters")
    @NotNull(message = "Address can not be empty")
    @Schema(name = "address", example = "Ligovsky Ave, 76, St Petersburg, Russia", required = true)
    private String address;

    @Size(max = 255, message = "Length of directions should not exceed 255 characters")
    @Schema(name = "directions", example = "Inside the supermarket")
    private String directions;

    @NotNull(message = "Shelf life days can not be empty")
    @Range(min = 1, max = 30, message = "Shelf life should be between 1 and 30 days")
    @Schema(name = "shelf_life_days", example = "7", required = true)
    private Byte shelfLifeDays;
}
