package com.gitlab.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PickupPointFeatures {

    TRY_ON_CLOTHES("Примерка одежды"),
    TRY_ON_SHOES("Примерка обуви"),
    DELIVERY_FOR_BUSINESSES("Доставка для юридических лиц"),
    PARTIAL_ORDER_REDEMPTION("Частичный выкуп заказа"),
    PRODUCT_RETURNS("Возврат товаров");

    private final String pickupPointFeatureRussianTranslation;
}