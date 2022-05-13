package com.example.booking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ScreeningNotFoundException extends RuntimeException{
    public ScreeningNotFoundException(String msg) {
        super(msg);
    }
}
