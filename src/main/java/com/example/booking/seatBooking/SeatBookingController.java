package com.example.booking.seatBooking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="api/v1/seatbooking")
public class SeatBookingController {

    private final SeatBookingService service;

    @Autowired
    public SeatBookingController(SeatBookingService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<SeatBookingDto>> getSeatBookings(){
        return ResponseEntity.ok().body(this.service.getSeatBookings().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping(path="/{bookingId}")
    public ResponseEntity<SeatBookingDto> getSeatBooking(@PathVariable long bookingId){
        return ResponseEntity.ok().body(convertToDTO(this.service.getSeatBooking(bookingId)));
    }

    @PostMapping
    public ResponseEntity<SeatBookingDto> addSeatBooking(@RequestBody SeatBookingDto seatBookingDto){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/seatBooking").toUriString());
        return ResponseEntity.created(uri).body(convertToDTO(this.service.addSeatBooking(seatBookingDto)));
    }

    @DeleteMapping(path="/{bookingId}")
    public ResponseEntity deleteSeatBooking(@PathVariable long bookingId){
        try {
            this.service.deleteSeatBooking(bookingId);
            return ResponseEntity.noContent().build();
        }
        catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping(path="/{bookingId}")
    public ResponseEntity<SeatBookingDto> updateSeatBooking(@PathVariable Long bookingId,
                                            @RequestBody SeatBookingDto seatBookingDto){

        try{
            return ResponseEntity.ok()
                    .body(convertToDTO(this.service.updateSeatBooking(bookingId, seatBookingDto)));
        }
        catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }

    }

    public SeatBookingDto convertToDTO(SeatBooking seatBooking){
        return SeatBookingDto.builder()
                .seatBookingId(seatBooking.getSeatBookingId())
                .seatNumber(seatBooking.getSeatAuditorium().getSeat().getSeatId().getSeatNumber())
                .rowNumber(seatBooking.getSeatAuditorium().getSeat().getSeatId().getRowNumber())
                .auditoriumId(seatBooking.getSeatAuditorium().getAuditorium().getAuditoriumId())
                .screeningId(seatBooking.getScreening().getScreeningId())
                .accountId(seatBooking.getAccount().getAccountId())
                .bookedTime(seatBooking.getBookedTime())
                .build();
    }
}
