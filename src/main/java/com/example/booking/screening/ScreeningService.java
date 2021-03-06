package com.example.booking.screening;

import com.example.booking.auditorium.Auditorium;
import com.example.booking.auditorium.AuditoriumRepository;
import com.example.booking.exception.BadRequestException;
import com.example.booking.exception.ScreeningNotFoundException;
import com.example.booking.movie.Movie;
import com.example.booking.movie.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final AuditoriumRepository auditoriumRepository;
    private final MovieRepository movieRepository;

    @Autowired
    public ScreeningService(ScreeningRepository screeningRepository,
                            AuditoriumRepository auditoriumRepository,
                            MovieRepository movieRepository) {

        this.screeningRepository = screeningRepository;
        this.auditoriumRepository = auditoriumRepository;
        this.movieRepository = movieRepository;
    }

    public List<Screening> getScreenings() {
        return this.screeningRepository.findAll();
    }

    public Boolean isScreeningExist(long auditoriumId, long showtime, long duration){

       return this.screeningRepository.findScreeningThatClashesBetweenShowTime(auditoriumId, showtime, duration*60).isPresent();
    }

    @Transactional
    public Screening addScreening(ScreeningDto screeningDto) {

        Screening newScreening = Screening.builder().build();

        Movie movie = this.movieRepository.findById(screeningDto.getMovieId()).orElseThrow(
                ()-> new BadRequestException(
                        "id " + screeningDto.getMovieId() + " does not exist for movie table."));

        if(isScreeningExist(screeningDto.getAuditoriumId(), screeningDto.getShowTime(), movie.getDuration())){
            throw new BadRequestException("Existing showtime for this auditorium for timing " + screeningDto.getShowTime()
                + " in auditorium id: " + screeningDto.getAuditoriumId());
        }

        Auditorium auditorium = this.auditoriumRepository.findById(screeningDto.getAuditoriumId()).orElseThrow(
                ()-> new BadRequestException(
                    "id " + screeningDto.getAuditoriumId() + " does not exist for Auditorium table."));

        newScreening.setAuditorium(auditorium);
        newScreening.setMovie(movie);
        newScreening.setShowTime(screeningDto.getShowTime());

        return this.screeningRepository.save(newScreening);

    }

    public Screening getScreeningById(Long screeningId) {

        return this.screeningRepository.findById(screeningId)
                .orElseThrow(() -> new ScreeningNotFoundException(
                        "id " + screeningId + " does not exist for screening table."));
    }

    public void deleteScreening(Long screeningId) {

        boolean isExists = this.screeningRepository.existsById(screeningId);
        if(!isExists)
            throw new BadRequestException(
                    "id " + screeningId + " does not exist for screening table.");

        this.screeningRepository.deleteById(screeningId);
    }

    public Screening updateScreening(Long screeningId, ScreeningDto screeningDto) {

        Long showTime = screeningDto.getShowTime();
        Long movieId = screeningDto.getMovieId();
        Long auditoriumId = screeningDto.getAuditoriumId();

        Movie movie;
        Auditorium auditorium;
        long newShowTime;

        Screening screening = this.screeningRepository.findById(screeningId).orElseThrow(
                ()-> new BadRequestException(
                        "screening of Id " + screeningId + " does not exists"));

        if(movieId!=null){
            movie = this.movieRepository.findById(movieId).orElseThrow(
                    ()-> new BadRequestException(
                            "id " + movieId + " does not exist for movie table."));
        }
        else{
            movie = screening.getMovie();
        }

        if(auditoriumId!=null){
            auditorium = this.auditoriumRepository.findById(auditoriumId).orElseThrow(
                    ()-> new BadRequestException(
                            "id " + auditoriumId + " does not exist for auditorium table."));
        }
        else{
            auditorium = screening.getAuditorium();
        }

        if(showTime != null &&
                showTime > 0 &&
                showTime!=screening.getShowTime()){
            newShowTime = showTime;
        }
        else{
            newShowTime = screening.getShowTime();
        }


        if(isScreeningExist(auditorium.getAuditoriumId(), newShowTime, movie.getDuration())){
            throw new BadRequestException("Existing showtime for this auditorium for timing " + newShowTime
                    + " in auditorium id: " + auditorium.getAuditoriumId());
        }

        screening.setMovie(movie);
        screening.setAuditorium(auditorium);
        screening.setShowTime(newShowTime);

        return this.screeningRepository.save(screening);

    }
}
