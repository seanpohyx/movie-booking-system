package com.example.booking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SeatBookingNotFoundException extends RuntimeException{
    public SeatBookingNotFoundException(String msg) {
        super(msg);
    }
}