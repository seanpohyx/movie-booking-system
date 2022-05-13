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
    public void updateMovie(Long id, String title, String description, Integer duration, String casts,
                             LocalDate startDate, LocalDate endDate) {

        long epochTimeNow = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC.of("+08:00"));

        Movie movie = this.repository.findById(id).orElseThrow(
                ()-> new BadRequestException(
                        "Movie with Id " + id + " does not exists"));

        if(title != null &&
                title.length() > 0 &&
                !Objects.equals(movie.getTitle(), title)){
            movie.setTitle(title);
        }

        if(description != null &&
                description.length() > 0 &&
                !Objects.equals(movie.getDescription(), description)){
            movie.setDescription(description);
        }

        if(duration != null &&
                duration > 0 &&
                duration != movie.getDuration()){
            movie.setDuration(duration);
        }

        if(casts != null &&
                casts.length() > 0 &&
                !Objects.equals(movie.getCasts(), casts)){
            movie.setCasts(casts);
        }

        if(startDate != null &&
                !movie.getStartDate().equals(startDate)){
            movie.setStartDate(startDate);
        }

        if(endDate != null &&
                !movie.getEndDate().equals(endDate)){
            movie.setEndDate(endDate);
        }

        movie.setUpdatedDateTime(epochTimeNow);

        this.repository.save(movie);

    }

    public Movie getMovieById(long movieId) {

        return this.repository.findById(movieId).orElseThrow(
                () -> new MovieNotFoundException(
                        "Movie with Id " + movieId + " does not exists"));
    }
}
