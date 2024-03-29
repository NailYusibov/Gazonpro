package com.gitlab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.ReadOnlyProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategoryDto {

    @ReadOnlyProperty
    private Long id;

    @NotEmpty(message = "ProductCategory name should not be empty")
    @Size(max = 30, message = "Length of ProductCategory name should be between 1 and 30 characters")
    private String name;

}
