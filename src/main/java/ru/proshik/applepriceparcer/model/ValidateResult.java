package ru.proshik.applepriceparcer.model;

public class ValidateResult {

    private boolean result;
    private String message;

    public ValidateResult(boolean result, String message) {
        this.result = result;
        this.message = message;
    }

    public boolean isResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }
}
