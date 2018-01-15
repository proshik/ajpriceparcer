package ru.proshik.applepriceparcer.model;

import java.util.Arrays;

public enum OperationType {

    PRICES("Prices"),
    HISTORY("History"),
    COMPARE("Compare"),
    SUBSCRIPTION("Subscription"),
    MAIN_MENU("Main menu");

    private final String value;

    OperationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static OperationType fromValue(String value) {
        return Arrays.stream(values())
                .filter(operationType -> operationType.getValue().toUpperCase().equals(value.toUpperCase()))
                .findFirst()
                .orElseThrow(null);
    }
}
