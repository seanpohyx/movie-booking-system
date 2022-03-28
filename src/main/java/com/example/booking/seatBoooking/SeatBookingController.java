package com.example.booking.seatBoooking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path="api/v1/seatbooking")
public class SeatBookingController {

    private final SeatBookingService service;

    @Autowired
    public SeatBookingController(SeatBookingService service) {
        this.service = service;
    }

    @GetMapping
    public List<SeatBooking> getSeatBooking(){
        return this.service.getSeatBooking();
    }
}
