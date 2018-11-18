package ru.proshik.applepricebot.exception;


public class ProviderParseException extends RuntimeException {

    public ProviderParseException(Throwable cause) {
        super(cause);
    }

    public ProviderParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
