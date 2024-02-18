package com.gitlab.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Gender {

    MALE("МУЖСКОЙ"),
    FEMALE("ЖЕНСКИЙ"),
    NOT_SPECIFIED("НЕ УКАЗАН");

    private final String sexRussianTranslation;
}