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
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path="/{bookingId}")
    public ResponseEntity updateSeatBooking(@PathVariable Long bookingId,
                                            @RequestParam(required = false) String rowNumber,
                                            @RequestParam(required = false) Integer seatNumber,
                                            @RequestParam(required = false) Long auditoriumId,
                                            @RequestParam(required = false) Long screeningId,
                                            @RequestParam(required = false) Long userId,
                                            @RequestParam(required = false) Long bookingTime){

        try{
            this.service.updateSeatBooking(bookingId, rowNumber, seatNumber,
                    auditoriumId, screeningId, userId, bookingTime);
            return ResponseEntity.noContent().build();
        }
        catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }

    }

    public SeatBookingDto convertToDTO(SeatBooking seatBooking){
        return SeatBookingDto.builder()
//                .seatNumber(seatBooking.getSeatAuditoriumCK().getSeatId().getSeatNumber())
//                .rowNumber(seatBooking.getSeatAuditoriumCK().getSeatId().getRowNumber())
//                .auditoriumId(seatBooking.getSeatAuditoriumCK().getAuditoriumId())
                .screeningId(seatBooking.getScreening().getScreeningId())
                .accountId(seatBooking.getAccount().getAccountId())
                .bookedTime(seatBooking.getBookedTime())
                .build();
    }
}
