package com.gitlab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Setter;

import java.util.Set;

@Data
@Setter
public class StoreDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private Set<Long> managersId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<Long> productsId;

    private Long ownerId;
}

