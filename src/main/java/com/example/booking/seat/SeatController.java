package com.example.booking.seat;

import com.example.booking.movie.Movie;
import com.example.booking.movie.MovieDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="api/v1/seat")
public class SeatController {

    private final SeatService service;
    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public SeatController(SeatService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<SeatDto>> getSeat(){
        return ResponseEntity.ok().body(this.service.getSeat().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity addSeats(@RequestBody SeatDto seatDto){

        try{
            return ResponseEntity.ok().body(convertToDTO(this.service.addNewSeat(seatDto)));
        }
        catch (IllegalStateException e){
            return  ResponseEntity.badRequest().body(e.getMessage());
        }

    }


    public Seat convertToEntity(SeatDto postDTO){
        return this.modelMapper.map(postDTO, Seat.class);
    }

    public SeatDto convertToDTO(Seat seat) {
        return this.modelMapper.map(seat, SeatDto.class);
    }
}
