package com.example.booking.movie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MovieRepositoryTest {

    private final MovieRepository underTest;
    private List<Movie> movies;

    @Autowired
    MovieRepositoryTest(MovieRepository underTest) {
        this.underTest = underTest;
    }

    @BeforeEach
    public void setUp(){

        long epochTimeNow = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC.of("+08:00"));
        Movie movie1 = Movie.builder()
                .createdDateTime(epochTimeNow)
                .duration(60 + 60 + 52)
                .startDate(LocalDate.of(2022, Month.MARCH, 02))
                .endDate(LocalDate.of(2022, Month.MAY, 10))
                .title("The batman")
                .casts("Robert Pattison")
                .updatedDateTime(epochTimeNow)
                .description("When the Riddler, a sadistic serial killer, begins murdering key political figures in Gotham, Batman is forced to investigate the city's hidden corruption and question his family's involvement.")
                .build();

        Movie movie2 = Movie.builder()
                .createdDateTime(epochTimeNow)
                .duration(60 + 56)
                .startDate(LocalDate.of(2022, Month.MARCH, 02))
                .endDate(LocalDate.of(2022, Month.MAY, 02))
                .title("Uncharted")
                .casts("Tom Holland")
                .updatedDateTime(epochTimeNow)
                .description("Treasure hunter Victor \"Sully\" Sullivan recruits street-smart Nathan Drake to help him recover a 500-year-old lost fortune amassed by explorer Ferdinand Magellan. What starts out as a heist soon becomes a globe-trotting, white-knuckle race to reach the prize before the ruthless Santiago Moncada can get his hands on it. If Sully and Nate can decipher the clues and solve one of the world's oldest mysteries, they stand to find $5 billion in treasure -- but only if they can learn to work together.")
                .build();

        this.movies = List.of(movie1, movie2);
        this.underTest.saveAll(this.movies);

    }

    @DisplayName("retrieve now showing success")
    @Test
    void givenLocateDate_whenFindNowShowing_thenReturnMovies(){

        //given
        //testing date
        LocalDate testingDate = LocalDate.of(2022, Month.MAY, 05);

        //when
        List<Movie> testMovies = this.underTest.findNowShowing(testingDate);

        //then
        assertThat(testMovies).isNotNull();
        assertThat(testMovies.size()).isEqualTo(1);
    }

    @DisplayName("retrieve now showing fail")
    @Test
    void givenLocateDate_whenFindNowShowing_thenReturnNull(){

        //given
        //testing date
        LocalDate testingDate = LocalDate.of(2022, Month.JUNE, 02);

        //when
        List<Movie> testMovies = this.underTest.findNowShowing(testingDate);

        //then
        assertThat(testMovies.size()).isEqualTo(0);
    }

}