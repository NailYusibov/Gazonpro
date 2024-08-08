package com.gitlab.util;

import lombok.experimental.UtilityClass;

import java.util.function.Consumer;

@UtilityClass
public class ServiceUtils {

    public static <T> void updateFieldIfNotNull(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
