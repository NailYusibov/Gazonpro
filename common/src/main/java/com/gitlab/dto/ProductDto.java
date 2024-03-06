package com.gitlab.dto;

import lombok.Data;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.ReadOnlyProperty;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Setter
public class ProductDto {

    @ReadOnlyProperty
    private Long id;

    @NotEmpty(message = "Product's name should not be empty")
    @Size(min = 3, max = 256, message = "Length of Product's name should be between 3 and 256 characters")
    private String name;

    @NotEmpty(message = "Product's description should not be empty")
    @Size(min = 3, max = 1000, message = "Length of Product's description should be between 3 and 1000 characters")
    private String description;

    @Range(min = 1, max = 2147483333, message = "Product's stockCount should be between 1 and 2147483333")
    @NotNull(message = "Product's stockCount should not be empty")
    private Integer stockCount;

    @ReadOnlyProperty
    private Long[] imagesId;

    @NotNull(message = "Product's isAdult field should not be empty")
    private Boolean isAdult;

    @NotEmpty(message = "Product's code should not be empty")
    @Size(min = 2, max = 256, message = "Length of Product's code should be between 2 and 256 characters")
    private String code;

    @Range(min = 1, max = 2147483333, message = "Product's weight should be between 1 and 2147483333")
    @NotNull(message = "Product's weight should not be empty")
    private Long weight;

    @DecimalMin(value = "0.1", message = "Product's price should be between 0.1 and 2147483333")
    @DecimalMax(value = "2147483333", message = "Product's price should be between 0.1 and 2147483333")
    @NotNull(message = "Product's price should not be empty")
    private BigDecimal price;

    private Long storeId;

    private String rating;

    private String productCategory;

    private Map<String, String> features;
}