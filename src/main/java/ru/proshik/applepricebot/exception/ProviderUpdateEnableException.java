package ru.proshik.applepricebot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ProviderUpdateEnableException extends RuntimeException {

    public ProviderUpdateEnableException(String message) {
        super(message);
    }

}
