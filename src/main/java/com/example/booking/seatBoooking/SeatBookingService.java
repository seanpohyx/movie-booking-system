package com.example.booking.seatBoooking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatBookingService {

    private final SeatBookingRepository repository;

    @Autowired
    public SeatBookingService(SeatBookingRepository repository) {
        this.repository = repository;
    }

    public List<SeatBooking> getSeatBooking(){
        return this.repository.findAll();
    }
}
