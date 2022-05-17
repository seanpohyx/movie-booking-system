package com.example.booking.movie;

import com.example.booking.exception.BadRequestException;
import com.example.booking.exception.MovieNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;

@Service
public class MovieService {

    private final MovieRepository repository;

    @Autowired
    public MovieService(MovieRepository repository) {
        this.repository = repository;
    }

    public List<Movie> getMovies() {
        return this.repository.findAll();
    }

    public List<Movie> getNowShowing() {
        return this.repository.findNowShowing(LocalDate.now());
    }

    public Movie addNewMovie(Movie movie) {
        long epochTimeNow = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC.of("+08:00"));
        movie.setUpdatedDateTime(epochTimeNow);
        movie.setCreatedDateTime(epochTimeNow);
        return this.repository.save(movie);
    }

    public void deleteMovie(Long id) {
        boolean isExists = this.repository.existsById(id);
        if(!isExists)
            throw new BadRequestException(
                    "Movie with Id " + id + " does not exists");

        this.repository.deleteById(id);
    }

    @Transactional
    public Movie updateMovie(Long id, Movie newMovie) {

        long epochTimeNow = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC.of("+08:00"));

        Movie movie = this.repository.findById(id).orElseThrow(
                ()-> new BadRequestException(
                        "Movie with Id " + id + " does not exists"));

        newMovie.setMovieId(id);
        newMovie.setUpdatedDateTime(epochTimeNow);
        return this.repository.save(newMovie);

    }

    public Movie getMovieById(long movieId) {

        return this.repository.findById(movieId).orElseThrow(
                () -> new MovieNotFoundException(
                        "Movie with Id " + movieId + " does not exists"));
    }
}
