package com.example.booking.screening;

import com.example.booking.auditorium.Auditorium;
import com.example.booking.auditorium.AuditoriumRepository;
import com.example.booking.exception.BadRequestException;
import com.example.booking.exception.ScreeningNotFoundException;
import com.example.booking.movie.Movie;
import com.example.booking.movie.MovieRepository;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ScreeningServiceTest {

    @Mock
    private ScreeningRepository screeningRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private AuditoriumRepository auditoriumRepository;

    @InjectMocks
    private ScreeningService underTest;

    public static final ZoneOffset offset = ZoneOffset.UTC.of("+08:00");
    public static final LocalDateTime FIXED_DATETIME = LocalDateTime.of(2022, Month.MARCH, 01, 8, 00, 00);
    private Screening screening;
    private Auditorium auditorium;
    private Movie movie;

    @BeforeEach
    void setUp(){
        long epochTimeNow = LocalDateTime.now().toEpochSecond(offset);
        long showTime = FIXED_DATETIME.toEpochSecond(offset);

        this.auditorium = Auditorium.builder()
                .auditoriumId(1L)
                .numberOfSeats(10)
                .build();

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

        this.screening = Screening.builder()
                .screeningId(1L)
                .movie(this.movie)
                .auditorium(this.auditorium)
                .showTime(showTime)
                .build();
    }

    @Test
    @DisplayName("Get all Screening")
    void given_whenGetScreening_thenReturnListOfScreening() {
        //given
        given(this.screeningRepository.findAll()).willReturn(List.of(this.screening));

        //when
        List<Screening> screenings = this.underTest.getScreenings();

        //then
        assertThat(screenings.size()).isEqualTo(1);
        assertThat(screenings.get(0)).isEqualTo(this.screening);
    }

    @Test
    @DisplayName("Is screening exist - return true")
    void givenAuditoriumIdShowtimeDuration_whenIsScreeningExist_ThenReturnTrue() {

        //given
        long auditoriumId = 1L;
        long showtime = FIXED_DATETIME.toEpochSecond(offset);
        long duration = 172L; //in mins

        given(this.screeningRepository.findScreeningThatClashesBetweenShowTime(auditoriumId, showtime, duration*60))
                .willReturn(Optional.of(this.screening));
        //when
        boolean isExist = this.underTest.isScreeningExist(auditoriumId, showtime, duration);

        //then
        assertThat(isExist).isTrue();

    }

    @Test
    @DisplayName("Is screening exist - return false")
    void givenAuditoriumIdShowtimeDuration_whenIsScreeningExist_ThenReturnFalse() {

        //given
        long auditoriumId = 1L;
        long showtime = FIXED_DATETIME.toEpochSecond(offset);
        long duration = 172L; //in mins

        given(this.screeningRepository.findScreeningThatClashesBetweenShowTime(auditoriumId, showtime, duration*60))
                .willReturn(Optional.empty());
        //when
        boolean isExist = this.underTest.isScreeningExist(auditoriumId, showtime, duration);

        //then
        assertThat(isExist).isFalse();

    }

    @Test
    @DisplayName("Add screening")
    void givenScreeningDto_whenAddScreening_thenReturnScreening() {
        //given
        long auditoriumId = 1L;
        long movieId = 1L;
        long showtime = FIXED_DATETIME.toEpochSecond(offset);

        ScreeningDto screeningDto = ScreeningDto.builder()
                .auditoriumId(auditoriumId)
                .movieId(movieId)
                .showTime(showtime)
                .build();

        Screening screeningInput = Screening.builder()
                .auditorium(this.auditorium)
                .movie(this.movie)
                .showTime(showtime)
                .build();

        given(this.movieRepository.findById(movieId)).willReturn(Optional.of(this.movie));
        given(this.screeningRepository.findScreeningThatClashesBetweenShowTime(auditoriumId, showtime, this.movie.getDuration()*60))
                .willReturn(Optional.empty());
        given(this.auditoriumRepository.findById(auditoriumId)).willReturn(Optional.of(this.auditorium));
        given(this.screeningRepository.save(screeningInput)).willReturn(this.screening);
        //when
        Screening testScreening = this.underTest.addScreening(screeningDto);

        //then
        assertThat(testScreening.getMovie()).isEqualTo(this.movie);
        assertThat(testScreening.getAuditorium()).isEqualTo(this.auditorium);
        assertThat(testScreening.getShowTime()).isEqualTo(showtime);

    }

    @Test
    @DisplayName("Add screening - throws exception for not existing movie Id")
    void givenScreeningDto_whenAddScreening_thenThrowExceptionsForNonExistentMovieId() {
        //given
        long auditoriumId = 1L;
        long movieId = 1L;
        long showtime = FIXED_DATETIME.toEpochSecond(offset);

        ScreeningDto screeningDto = ScreeningDto.builder()
                .auditoriumId(auditoriumId)
                .movieId(movieId)
                .showTime(showtime)
                .build();

        given(this.movieRepository.findById(movieId)).willReturn(Optional.empty());
        //when

        //then
        assertThatThrownBy(()->this.underTest.addScreening(screeningDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("id " + screeningDto.getMovieId() + " does not exist for movie table.");

    }

    @Test
    @DisplayName("Add screening - throws exception for existing screening time")
    void givenScreeningDto_whenAddScreening_thenThrowExceptionsForScreeningTime() {
        //given
        long auditoriumId = 1L;
        long movieId = 1L;
        long showtime = FIXED_DATETIME.toEpochSecond(offset);

        ScreeningDto screeningDto = ScreeningDto.builder()
                .auditoriumId(auditoriumId)
                .movieId(movieId)
                .showTime(showtime)
                .build();

        given(this.movieRepository.findById(movieId)).willReturn(Optional.of(this.movie));
        given(this.screeningRepository.findScreeningThatClashesBetweenShowTime(auditoriumId, showtime, this.movie.getDuration()*60))
                .willReturn(Optional.of(this.screening));
        //when

        //then
        assertThatThrownBy(()->this.underTest.addScreening(screeningDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Existing showtime for this auditorium for timing " + screeningDto.getShowTime()
                        + " in auditorium id: " + screeningDto.getAuditoriumId());

    }

    @Test
    @DisplayName("Add screening - throws exception for auditorium not exist")
    void givenScreeningDto_whenAddScreening_thenThrowsExceptionForAuditoriumDoesNotExist() {
        //given
        long auditoriumId = 1L;
        long movieId = 1L;
        long showtime = FIXED_DATETIME.toEpochSecond(offset);

        ScreeningDto screeningDto = ScreeningDto.builder()
                .auditoriumId(auditoriumId)
                .movieId(movieId)
                .showTime(showtime)
                .build();

        given(this.movieRepository.findById(movieId)).willReturn(Optional.of(this.movie));
        given(this.screeningRepository.findScreeningThatClashesBetweenShowTime(auditoriumId, showtime, this.movie.getDuration()*60))
                .willReturn(Optional.empty());
        given(this.auditoriumRepository.findById(auditoriumId)).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(()->this.underTest.addScreening(screeningDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("id " + screeningDto.getAuditoriumId() + " does not exist for Auditorium table.");
    }

    @Test
    @DisplayName("Get screening by Id")
    void givenScreeningId_whenGetScreeningById_thenReturnScreening() {
        //given
        long screeningId = 1;

        given(this.screeningRepository.findById(screeningId)).willReturn(Optional.of(this.screening));

        //when
        Screening testScreening = this.underTest.getScreeningById(screeningId);

        //then
        assertThat(testScreening).isEqualTo(this.screening);
    }

    @Test
    @DisplayName("Get screening by Id - throws exception for invalid screeningId")
    void givenScreeningId_whenGetScreeningById_thenThrowsExceptionsForInvalidScreeningId() {
        //given
        long screeningId = 1;

        given(this.screeningRepository.findById(screeningId)).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> this.underTest.getScreeningById(screeningId))
                .isInstanceOf(ScreeningNotFoundException.class)
                .hasMessageContaining(
                        "id " + screeningId + " does not exist for screening table.");
    }

    @Test
    @DisplayName("Delete screening")
    void givenScreeningId_whenDeleteScreening_thenDoNothing() {
        //given
        long screeningId = 1;

        given(this.screeningRepository.existsById(screeningId)).willReturn(true);

        //when
        this.underTest.deleteScreening(screeningId);

        //then
        verify(this.screeningRepository, times(1)).deleteById(screeningId);
    }

    @Test
    @DisplayName("Delete screening - throws exceptions for invalid screening id")
    void givenScreeningId_whenDeleteScreening_thenThrowsExceptionsForInvalidScreeningId() {
        //given
        long screeningId = 1;

        given(this.screeningRepository.existsById(screeningId)).willReturn(false);

        //when
        //then
        assertThatThrownBy(() -> this.underTest.deleteScreening(screeningId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(
                        "id " + screeningId + " does not exist for screening table.");

    }

    @Test
    @DisplayName("Update screening")
    void givenScreeningIdShowTimeMovieIdAuditoriumId_whenUpdateScreening_thenDoNothing() {
        //given
        long screeningId = 1L;
        long showTime = FIXED_DATETIME.minusHours(2).toEpochSecond(offset);
        long movieId = 1L;
        long auditoriumId = 1L;

        given(this.screeningRepository.findById(screeningId)).willReturn(Optional.of(this.screening));
        given(this.movieRepository.findById(movieId)).willReturn(Optional.of(this.movie));
        given(this.auditoriumRepository.findById(auditoriumId)).willReturn(Optional.of(this.auditorium));
        given(this.screeningRepository.findScreeningThatClashesBetweenShowTime(this.auditorium.getAuditoriumId(), showTime, this.movie.getDuration() * 60))
                .willReturn(Optional.empty());

        //when
        this.underTest.updateScreening(screeningId, ScreeningDto.builder()
                        .auditoriumId(auditoriumId)
                        .screeningId(screeningId)
                        .movieId(movieId)
                        .showTime(showTime)
                .build());

        //then
        ArgumentCaptor<Screening> screeningArgumentCaptor =
                ArgumentCaptor.forClass(Screening.class);

        verify(this.screeningRepository).save(screeningArgumentCaptor.capture());

        Screening testScreening = screeningArgumentCaptor.getValue();
        assertThat(testScreening.getScreeningId()).isEqualTo(screeningId);
        assertThat(testScreening.getShowTime()).isEqualTo(showTime);
        assertThat(testScreening.getMovie()).isEqualTo(this.movie);
        assertThat(testScreening.getAuditorium()).isEqualTo(this.auditorium);
    }

    @Test
    @DisplayName("Update screening - throw exceptions for invalid screening Id")
    void givenScreeningIdShowTimeMovieIdAuditoriumId_whenUpdateScreening_thenThrowExceptionForScreeningId(){
        //given
        long screeningId = 1L;
        long showTime = FIXED_DATETIME.minusHours(2).toEpochSecond(offset);
        long movieId = 1L;
        long auditoriumId = 1L;

        given(this.screeningRepository.findById(screeningId)).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> this.underTest.updateScreening(screeningId, ScreeningDto.builder()
                .auditoriumId(auditoriumId)
                .screeningId(screeningId)
                .movieId(movieId)
                .showTime(showTime)
                .build()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("screening of Id " + screeningId + " does not exists");
    }

    @Test
    @DisplayName("Update screening - throw exceptions for invalid movieId")
    void givenScreeningIdShowTimeMovieIdAuditoriumId_whenUpdateScreening_thenThrowExceptionForMovieId(){
        //given
        long screeningId = 1L;
        long showTime = FIXED_DATETIME.minusHours(2).toEpochSecond(offset);
        long movieId = 1L;
        long auditoriumId = 1L;

        given(this.screeningRepository.findById(screeningId)).willReturn(Optional.of(this.screening));
        given(this.movieRepository.findById(movieId)).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> this.underTest.updateScreening(screeningId, ScreeningDto.builder()
                .auditoriumId(auditoriumId)
                .screeningId(screeningId)
                .movieId(movieId)
                .showTime(showTime)
                .build()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("id " + movieId + " does not exist for movie table.");
    }

    @Test
    @DisplayName("Update screening - throw exceptions for invalid auditoriumId")
    void givenScreeningIdShowTimeMovieIdAuditoriumId_whenUpdateScreening_thenThrowExceptionForAuditoriumId(){
        //given
        long screeningId = 1L;
        long showTime = FIXED_DATETIME.minusHours(2).toEpochSecond(offset);
        long movieId = 1L;
        long auditoriumId = 1L;

        given(this.screeningRepository.findById(screeningId)).willReturn(Optional.of(this.screening));
        given(this.movieRepository.findById(movieId)).willReturn(Optional.of(this.movie));
        given(this.auditoriumRepository.findById(auditoriumId)).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> this.underTest.updateScreening(screeningId, ScreeningDto.builder()
                .auditoriumId(auditoriumId)
                .screeningId(screeningId)
                .movieId(movieId)
                .showTime(showTime)
                .build()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("id " + auditoriumId + " does not exist for auditorium table.");
    }

    @Test
    @DisplayName("Update screening - throw exceptions for existing showtime")
    void givenScreeningIdShowTimeMovieIdAuditoriumId_whenUpdateScreening_thenThrowExceptionForExistingShowtime(){
        //given
        long screeningId = 1L;
        long showTime = FIXED_DATETIME.minusHours(2).toEpochSecond(offset);
        long movieId = 1L;
        long auditoriumId = 1L;

        given(this.screeningRepository.findById(screeningId)).willReturn(Optional.of(this.screening));
        given(this.movieRepository.findById(movieId)).willReturn(Optional.of(this.movie));
        given(this.auditoriumRepository.findById(auditoriumId)).willReturn(Optional.of(this.auditorium));
        given(this.screeningRepository.findScreeningThatClashesBetweenShowTime(this.auditorium.getAuditoriumId(), showTime, this.movie.getDuration() * 60))
                .willReturn(Optional.of(this.screening));

        //when
        //then
        assertThatThrownBy(() -> this.underTest.updateScreening(screeningId, ScreeningDto.builder()
                .auditoriumId(auditoriumId)
                .screeningId(screeningId)
                .showTime(showTime)
                .movieId(movieId)
                .build()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Existing showtime for this auditorium for timing " + showTime
                        + " in auditorium id: " + this.auditorium.getAuditoriumId());
    }
}