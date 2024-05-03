package com.gitlab.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.ReadOnlyProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ReviewImageUpdateDto {

    @ReadOnlyProperty
    private Long id;

    @NotNull(message = "ReviewImage's reviewId should not be empty")
    private Long reviewId;

    @Size(max = 256, message = "Length of ReviewImage's name should be between 1 and 256 characters")
    private String name;

    private byte[] data;
}
