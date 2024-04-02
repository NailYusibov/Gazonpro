package com.gitlab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.util.Set;

@Data
@Setter
public class StoreDto {

    @ReadOnlyProperty
    private Long id;

    private Set<Long> managersId;

    private Long ownerId;
}

