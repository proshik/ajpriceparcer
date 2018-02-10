package ru.proshik.applepricebot.exception;

public class ProviderParseException extends ServiceLayerException {

    public ProviderParseException(Throwable cause) {
        super(cause);
    }

    public ProviderParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
