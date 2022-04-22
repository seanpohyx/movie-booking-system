package com.example.booking.seatAuditorium;

import org.springframework.beans.factory.annotation.Autowired;

public class SeatAuditoriumService {

    private final SeatAuditoriumRepository repository;

    @Autowired
    public SeatAuditoriumService(SeatAuditoriumRepository repository) {
        this.repository = repository;
    }
}
