package com.example.booking.movie;

import com.example.booking.exception.BadRequestException;
import com.example.booking.exception.MovieNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository repository;

    @InjectMocks
    private MovieService underTest;

    private Movie movie;

    @BeforeEach
    void setUp(){
        long epochTimeNow = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC.of("+08:00"));
        this.movie = Movie.builder()
                .movieId(1L)
                .createdDateTime(epochTimeNow)
                .duration(60 + 60 + 52)
                .startDate(LocalDate.of(2022, Month.MARCH, 02))
                .endDate(LocalDate.of(2022, Month.MAY, 10))
                .title("The batman")
                .casts("Robert Pattison")
                .updatedDateTime(epochTimeNow)
                .description("When the Riddler, a sadistic serial killer, begins murdering key political figures in Gotham, Batman is forced to investigate the city's hidden corruption and question his family's involvement.")
                .build();
    }

    @Test
    @DisplayName("Get all movies")
    void given_whenGetMovies_thenReturnListOfMovies(){
        //given
        given(this.repository.findAll()).willReturn(List.of(this.movie));

        //when
        List<Movie> testMovies = this.underTest.getMovies();

        //then
        assertThat(testMovies.size()).isEqualTo(1);
        assertThat(testMovies.get(0)).isEqualTo(this.movie);
    }

    @Test
    @DisplayName("Get now showing movies")
    void given_whenGetNowShowing_thenListOfMovie(){

        //given
        LocalDate timeNow = LocalDate.now();
        given(this.repository.findNowShowing(timeNow)).willReturn(List.of(this.movie));

        //when
        List<Movie> testMovies = this.underTest.getNowShowing();

        //then
        assertThat(testMovies.size()).isEqualTo(1);
        assertThat(testMovies.get(0)).isEqualTo(this.movie);

    }

    @Test
    @DisplayName("Add movie")
    void givenMovie_whenAddNewMovie_thenReturnMovie(){

        //given
        given(this.repository.save(this.movie)).willReturn(this.movie);

        //when
        Movie testMovie = this.underTest.addNewMovie(this.movie);

        //then
        assertThat(testMovie.getMovieId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Delete movie with Id")
    void givenId_whenDeleteMovie_thenReturnNull(){

        //given
        long id = 1L;

        given(this.repository.existsById(id)).willReturn(true);
        willDoNothing().given(this.repository).deleteById(id);

        //when
        this.underTest.deleteMovie(id);

        //then
        verify(this.repository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Delete movie with Id - throws exception for invalid movie id")
    void givenId_whenDeleteMovie_thenThrowExceptionsForInvalidMovieId(){

        //given
        long id = 1L;

        given(this.repository.existsById(id)).willReturn(false);

        //when
        //then
        assertThatThrownBy(()->this.underTest.deleteMovie(id))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Movie with Id " + id + " does not exists");
    }

    @Test
    @DisplayName("Update Movie")
    void givenIdTitleDescriptionDurationCastsStartDateEndDate_whenUpdateMovie_thenReturnNothing(){

        //given
        long id = 1L;
        String title = "Hell Kitchen";
        String description = "Cooking in Hell with Gordon";
        Integer duration = 120;
        String casts = "Gordon Ramsay";
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now().plusDays(10);

        given(this.repository.findById(id)).willReturn(Optional.of(this.movie));

        //when
        this.underTest.updateMovie(id, title, description, duration, casts, startDate, endDate);

        //then
        ArgumentCaptor<Movie> movieArgumentCaptor =
                ArgumentCaptor.forClass(Movie.class);

        verify(this.repository).save(movieArgumentCaptor.capture());//this is to capture the saved results

        Movie capturedMovie = movieArgumentCaptor.getValue();
        assertThat(capturedMovie.getTitle()).isEqualTo(title);
        assertThat(capturedMovie.getDescription()).isEqualTo(description);
        assertThat(capturedMovie.getDuration()).isEqualTo(duration);
        assertThat(capturedMovie.getCasts()).isEqualTo(casts);
        assertThat(capturedMovie.getStartDate()).isEqualTo(startDate);
        assertThat(capturedMovie.getEndDate()).isEqualTo(endDate);
    }

    @Test
    @DisplayName("Update Movie By Id - throws Exception for invalid movie id")
    void givenIdTitleDescriptionDurationCastsStartDateEndDate_whenUpdateMovie_thenThrowExceptionForInvalidMovieId(){

        //given
        long id = 1L;
        String title = "Hell Kitchen";
        String description = "Cooking in Hell with Gordon";
        Integer duration = 120;
        String casts = "Gordon Ramsay";
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now().plusDays(10);

        given(this.repository.findById(id)).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(()->this.underTest.updateMovie(id, title, description, duration, casts, startDate, endDate))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Movie with Id " + id + " does not exists");
    }

    @Test
    @DisplayName("Get Movie By Id - throws Exception for invalid movie id")
    void givenId_whenGetMovieById_thenThrowsExceptionsForInvalidMovieId(){
        long id = 1L;

        given(this.repository.findById(id)).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(()->this.underTest.getMovieById(id))
                .isInstanceOf(MovieNotFoundException.class)
                .hasMessageContaining("Movie with Id " + id + " does not exists");
    }


}