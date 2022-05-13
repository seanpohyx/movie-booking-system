package com.example.booking.seatAuditorium;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="api/v1/seatAuditorium")
public class SeatAuditoriumController {

    private final SeatAuditoriumService service;
    private final ModelMapper modelMapper;

    @Autowired
    public SeatAuditoriumController(SeatAuditoriumService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public ResponseEntity<List<SeatAuditoriumDto>> getSeatAuditoriums(){
        return ResponseEntity.ok().body(this.service.getSeatAuditoriumList().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));

    }

    @GetMapping(path="/{rowId}/{seatNumber}/{auditoriumId}")
    public ResponseEntity<SeatAuditoriumDto> getSeatAuditorium(@PathVariable Integer seatNumber,
                                                               @PathVariable String rowId,
                                                               @PathVariable Long auditoriumId){
        return ResponseEntity.ok().body(
                convertToDTO(this.service.getSeatAuditorium(seatNumber, rowId, auditoriumId)));
    }

    @PostMapping
    public ResponseEntity<SeatAuditoriumDto> addSeatAuditorium(@RequestBody SeatAuditoriumDto seatAuditoriumDto){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/seatAuditorium").toUriString());
        return ResponseEntity.created(uri).body(convertToDTO(this.service.addSeatAuditorium(seatAuditoriumDto)));
    }

    @DeleteMapping(path="/{rowId}/{seatNumber}/{auditoriumId}")
    public ResponseEntity deleteSeatAuditorium(@PathVariable Integer seatNumber,
                                               @PathVariable String rowId,
                                               @PathVariable Long auditoriumId){
        try {
            this.service.deleteSeatAuditorium(rowId, seatNumber, auditoriumId);
            return ResponseEntity.noContent().build();
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    public SeatAuditoriumDto convertToDTO(SeatAuditorium seatAuditorium) {
        return SeatAuditoriumDto.builder()
                .seatNumber(seatAuditorium.getSeat().getSeatId().getSeatNumber())
                .rowNumber(seatAuditorium.getSeat().getSeatId().getRowNumber())
                .auditoriumId(seatAuditorium.getAuditorium().getAuditoriumId())
                .build();
    }
}
