package com.example.booking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SeatAuditoriumNotFoundException extends RuntimeException{
    public SeatAuditoriumNotFoundException(String msg) {
        super(msg);
    }
}