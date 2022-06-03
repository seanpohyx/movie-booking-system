package com.example.booking.screening;

import com.example.booking.auditorium.Auditorium;
import com.example.booking.auditorium.AuditoriumRepository;
import com.example.booking.movie.Movie;
import com.example.booking.movie.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ScreeningRepositoryTest {

    public static final ZoneOffset offset = ZoneOffset.UTC.of("+08:00");
    public static final LocalDateTime FIXED_DATETIME = LocalDateTime.of(2022, Month.MARCH, 01, 8, 00, 00);

    private ScreeningRepository underTest;
    private AuditoriumRepository auditoriumRepository;
    private MovieRepository movieRepository;
    private Screening screening;

    @Autowired
    ScreeningRepositoryTest(ScreeningRepository underTest, AuditoriumRepository auditoriumRepository, MovieRepository movieRepository) {
        this.underTest = underTest;
        this.auditoriumRepository = auditoriumRepository;
        this.movieRepository = movieRepository;
    }

    @BeforeEach
    void setUp() {

        this.auditoriumRepository.deleteAll();

        long epochTimeNow = LocalDateTime.now().toEpochSecond(offset);
        long showTime = FIXED_DATETIME.toEpochSecond(offset);

        Auditorium auditorium = Auditorium.builder()
                .numberOfSeats(10)
                .build();

        this.auditoriumRepository.save(auditorium);

        Movie movie = Movie.builder()
                .createdDateTime(epochTimeNow)
                .duration(60 + 60 + 52)
                .startDate(LocalDate.of(2022, Month.MARCH, 02))
                .endDate(LocalDate.of(2022, Month.MAY, 10))
                .title("The batman")
                .casts("Robert Pattison")
                .updatedDateTime(epochTimeNow)
                .description("When the Riddler, a sadistic serial killer, begins murdering key political figures in Gotham, Batman is forced to investigate the city's hidden corruption and question his family's involvement.")
                .build();

        this.movieRepository.save(movie);

        this.screening = this.underTest.save(Screening.builder()
                .movie(movie)
                .auditorium(auditorium)
                .showTime(showTime)
                .build());
    }

    @Test
    void givenAuditoriumIdShowTimeDuration_whenExistsByAuditoriumIdAndShowTime_thenReturnScreening() {

        //init
        Movie movie = this.movieRepository.findAll().get(0);
        Auditorium auditorium = this.auditoriumRepository.findAll().get(0);

        //given
        long auditoriumId = auditorium.getAuditoriumId();
        long showtime = FIXED_DATETIME.toEpochSecond(offset);
        long duration = movie.getDuration();

        //when
        Screening screeningTest = this.underTest.findScreeningThatClashesBetweenShowTime(auditoriumId, showtime, duration).orElse(null);

        //then
        assertThat(screeningTest).isNotNull();
        assertThat(screeningTest).isEqualTo(this.screening);
    }

    @Test
    void givenAuditoriumIdShowTimeDuration_whenExistsByAuditoriumIdAndShowTime_thenReturnNull() {

        //init
        Movie movie = this.movieRepository.findAll().get(0);
        Auditorium auditorium = this.auditoriumRepository.findAll().get(0);

        //given
        long auditoriumId = auditorium.getAuditoriumId();
        long showtime = FIXED_DATETIME.minusMinutes(movie.getDuration()+10).toEpochSecond(offset);
        long duration = movie.getDuration();

        //when
        Screening screeningTest = this.underTest.findScreeningThatClashesBetweenShowTime(auditoriumId, showtime, duration).orElse(null);

        //then
        assertThat(screeningTest).isNull();
    }
}