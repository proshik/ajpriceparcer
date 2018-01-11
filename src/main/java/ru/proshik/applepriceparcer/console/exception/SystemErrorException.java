package ru.proshik.applepriceparcer.console.exception;

public class SystemErrorException extends RuntimeException {

    public SystemErrorException(String message) {
        super(message);
    }

    public SystemErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
