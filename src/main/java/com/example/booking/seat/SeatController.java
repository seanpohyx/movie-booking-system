package com.example.booking.seat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="api/v1/seat")
public class SeatController {

    private final SeatService service;

    @Autowired
    public SeatController(SeatService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<SeatDto>> getSeats(){
        return ResponseEntity.ok().body(this.service.getSeats().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{rowId}/{seatNumber}")
    public ResponseEntity<SeatDto> getSeat(@PathVariable String rowId, @PathVariable int seatNumber){
        return ResponseEntity.ok().body(convertToDTO(this.service.getSeat(seatNumber, rowId)));
    }

    @PostMapping
    public ResponseEntity<SeatDto> addSeat(@RequestBody SeatDto seatDto){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/seat").toUriString());
        return ResponseEntity.created(uri).body(convertToDTO(this.service.addNewSeat(convertToEntity(seatDto))));
    }

    @DeleteMapping(path="/{rowId}/{seatNumber}")
    public ResponseEntity deleteSeat(@PathVariable Integer seatNumber, @PathVariable String rowId){

        try {
            this.service.deleteSeat(rowId, seatNumber);
            return ResponseEntity.noContent().build();
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }

    }

    @PutMapping(path="/{rowId}/{seatId}")
    public ResponseEntity<SeatDto> updateSeat(@PathVariable String rowId,
                                     @PathVariable Integer seatId,
                                      @RequestBody SeatDto seatDto){

        try {
            return ResponseEntity.ok()
                    .body(convertToDTO(this.service.updateSeat(rowId, seatId, convertToEntity(seatDto))));
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }

    }

    public SeatDto convertToDTO(Seat seat) {
        return SeatDto.builder()
                .cost(seat.getCost())
                .rowNumber(seat.getSeatId().getRowNumber())
                .seatNumber(seat.getSeatId().getSeatNumber())
                .build();
    }

    public Seat convertToEntity(SeatDto seatDto) {
        return Seat.builder()
                .seatId(SeatId.builder()
                        .rowNumber(seatDto.getRowNumber())
                        .seatNumber(seatDto.getSeatNumber())
                        .build())
                .cost(seatDto.getCost())
                .build();
    }
}
