package com.example.booking.screening;

import com.example.booking.movie.Movie;
import com.example.booking.movie.MovieDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="api/v1/screening")
public class ScreeningController {

    private final ScreeningService service;
    private final ModelMapper modelMapper;

    @Autowired
    public ScreeningController(ScreeningService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<List<ScreeningDto>> getScreening(){
        return ResponseEntity.ok().body(this.service.getScreening().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping(path = "{screeningId}")
    public ResponseEntity<ScreeningDto> getScreeningById(@PathVariable Long screeningId){
        return ResponseEntity.ok().body(convertToDTO(this.service.getScreeningById(screeningId)));
    }

    @PostMapping
    public ResponseEntity<ScreeningDto> addScreening(@RequestBody ScreeningDto screeningDto){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/screening").toUriString());
        try {
            return ResponseEntity.created(uri).body(convertToDTO(this.service.addScreening(screeningDto)));
        }
        catch (IllegalStateException e){
            return ResponseEntity.badRequest().build();
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping(path = "{screeningId}")
    public ResponseEntity<ScreeningDto> updateScreening(@PathVariable Long screeningId,
                                @RequestParam(required = false) Long showTime,
                                @RequestParam(required = false) Long movieId,
                                @RequestParam(required = false) Long auditoriumId){

        try {
            return ResponseEntity.ok().body(convertToDTO(this.service.updateScreening(screeningId, showTime, movieId, auditoriumId)));
        }
        catch (IllegalStateException e){
            return ResponseEntity.badRequest().build();
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping(path = "{screeningId}")
    public ResponseEntity deleteScreening(@PathVariable Long screeningId){
        try{
            this.service.deleteScreening(screeningId);
            return ResponseEntity.noContent().build();
        }
        catch (IllegalStateException e){
            return ResponseEntity.badRequest().build();
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    public Screening convertToEntity(ScreeningDto postDTO){
        return this.modelMapper.map(postDTO, Screening.class);
    }

    public ScreeningDto convertToDTO(Screening screening) {
        return this.modelMapper.map(screening, ScreeningDto.class);
    }

}
