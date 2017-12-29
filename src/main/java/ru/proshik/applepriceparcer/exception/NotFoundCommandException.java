package ru.proshik.applepriceparcer.exception;

public class NotFoundCommandException extends NotFoundParameterException {

    public NotFoundCommandException(String message) {
        super(message);
    }
}
