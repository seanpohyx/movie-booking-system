package com.example.booking.movie;

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
@RequestMapping(path="api/v1/movie")
public class MovieController {

    private final MovieService service;
    private final ModelMapper modelMapper;

    @Autowired
    public MovieController(MovieService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<List<MovieDto>> getMovie(){
        return ResponseEntity.ok().body(this.service.getMovie().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping(path = {"{movieId}"})
    public ResponseEntity<MovieDto> getMovie(@PathVariable Long movieId){
        return ResponseEntity.ok().body(convertToDTO(this.service.getMovieById(movieId)));
    }

    @GetMapping(path="latest")
    public ResponseEntity<List<MovieDto>> getLatestMovie(){
        return ResponseEntity.ok().body(this.service.getNowShowing().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<MovieDto> addMovie(@RequestBody MovieDto movieDto){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/movie").toUriString());
        Movie movie = this.service.addNewMovie(convertToEntity(movieDto));
        return ResponseEntity.created(uri).body(convertToDTO(movie));
    }

    @DeleteMapping(path="{movieId}")
    public ResponseEntity deleteMovie(@PathVariable Long movieId){
        try{
            this.service.deleteMovie(movieId);
            return ResponseEntity.noContent().build();
        }
        catch (IllegalStateException e){
            return ResponseEntity.badRequest().build();
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping(path = "{movieId}")
    public ResponseEntity<MovieDto> updateMovie(@PathVariable Long movieId,
                            @RequestParam(required = false) String title,
                            @RequestParam(required = false) String description,
                            @RequestParam(required = false) Integer duration,
                            @RequestParam(required = false) String casts,
                            @RequestParam(required = false) LocalDate startDate,
                            @RequestParam(required = false) LocalDate endDateTime){

        try {
            this.service.updateMovie(movieId, title, description, duration, casts, startDate, endDateTime);
            return ResponseEntity.noContent().build();
        }
            catch (IllegalStateException e){
            return ResponseEntity.badRequest().build();
        }
            catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    public Movie convertToEntity(MovieDto postDTO){
        return this.modelMapper.map(postDTO, Movie.class);
    }

    public MovieDto convertToDTO(Movie movie) {
        return this.modelMapper.map(movie, MovieDto.class);
    }
}
