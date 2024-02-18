package com.gitlab.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EntityStatus {

    ACTIVE("Активный"),

    DELETED("Удалённый");

    private final String status;
}