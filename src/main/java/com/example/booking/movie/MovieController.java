package com.example.booking.movie;

import org.modelmapper.ModelMapper;
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
    public ResponseEntity<List<MovieDto>> getMovies(){
        return ResponseEntity.ok().body(this.service.getMovies().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping(path = {"{movieId}"})
    public ResponseEntity<MovieDto> getMovie(@PathVariable Long movieId){
        return ResponseEntity.ok().body(convertToDTO(this.service.getMovieById(movieId)));
    }

    @GetMapping(path="nowShowing")
    public ResponseEntity<List<MovieDto>> getNowShowing(){
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
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping(path = "{movieId}")
    public ResponseEntity<MovieDto> updateMovie(@PathVariable Long movieId,
                            @RequestBody MovieDto movieDto){

        try {
            return ResponseEntity.ok()
                    .body(convertToDTO(this.service.updateMovie(movieId, movieDto)));
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
