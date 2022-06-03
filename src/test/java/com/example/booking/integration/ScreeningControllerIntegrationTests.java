package com.example.booking.integration;

import com.example.booking.auditorium.Auditorium;
import com.example.booking.auditorium.AuditoriumRepository;
import com.example.booking.movie.Movie;
import com.example.booking.movie.MovieRepository;
import com.example.booking.screening.Screening;
import com.example.booking.screening.ScreeningDto;
import com.example.booking.screening.ScreeningRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ScreeningControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ScreeningRepository repository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private AuditoriumRepository auditoriumRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Screening screening;
    private Movie movie;
    private Auditorium auditorium;
    private ZoneOffset OFFSET = ZoneOffset.UTC.of("+08:00");
    private LocalDateTime FIXED_DATETIME = LocalDateTime.of(2022, Month.MARCH, 01, 8, 00, 00);

    @BeforeEach
    void setUp(){

        this.movieRepository.deleteAll();

        long epochTimeNow = LocalDateTime.now().toEpochSecond(OFFSET);
        long showTime = FIXED_DATETIME.toEpochSecond(OFFSET);

        this.auditorium = this.auditoriumRepository.findAll().get(0);

        this.movie = this.movieRepository.save(Movie.builder()
                .createdDateTime(epochTimeNow)
                .duration(60 + 60 + 52)
                .startDate(LocalDate.of(2022, Month.MARCH, 02))
                .endDate(LocalDate.of(2040, Month.MAY, 10))
                .title("The batman")
                .casts("Robert Pattison")
                .updatedDateTime(epochTimeNow)
                .description("When the Riddler, a sadistic serial killer, begins murdering key political figures in Gotham, Batman is forced to investigate the city's hidden corruption and question his family's involvement.")
                .build());

        this.screening = Screening.builder()
                .screeningId(1L)
                .movie(movie)
                .auditorium(auditorium)
                .showTime(showTime)
                .build();
    }

    @AfterEach
    void cleanUp(){
        this.repository.deleteAll();
    }

    @Test
    @DisplayName("Get Screening")
    void given_whenGetScreenings_thenReturnListOfScreening() throws Exception {

        //given
        this.repository.save(this.screening);

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/screening"));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(1)));

    }

    @Test
    @DisplayName("Get screening by Id")
    void givenScreeningId_whenGetScreeningById_thenReturnScreening() throws Exception {
        //given
        Screening screening = this.repository.save(this.screening);
        Long screeningId = screening.getScreeningId();

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/screening/{id}", screeningId));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.screeningId", is(screeningId), Long.class))
                .andExpect(jsonPath("$.movieId", is(screening.getMovie().getMovieId()), Long.class))
                .andExpect(jsonPath("$.auditoriumId", is(screening.getAuditorium().getAuditoriumId()), Long.class))
                .andExpect(jsonPath("$.showTime", is(screening.getShowTime()), Long.class));
    }

    @Test
    @DisplayName("Post Screening")
    void givenScreeningDto_whenAddScreening_thenReturnScreeningDto() throws Exception {
        //given
        long showTime = FIXED_DATETIME.plusHours(5).toEpochSecond(OFFSET);
        long movieId = this.movie.getMovieId();
        long auditoriumId = this.auditorium.getAuditoriumId();

        ScreeningDto screeningDto = ScreeningDto.builder()
                .showTime(showTime)
                .movieId(movieId)
                .auditoriumId(auditoriumId)
                .build();

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/screening")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screeningDto)));

        //then
        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.movieId", is(screeningDto.getMovieId()), Long.class))
                .andExpect(jsonPath("$.auditoriumId", is(screeningDto.getAuditoriumId()), Long.class))
                .andExpect(jsonPath("$.showTime", is(screeningDto.getShowTime()), Long.class));

    }

    @Test
    @DisplayName("Update screening by Id")
    void givenScreeningIdShowTimeMovieIdAuditoriumId_whenUpdateScreening_thenReturnScreeningDto() throws Exception {
        //given

        Screening screening = this.repository.save(this.screening);
        Long screeningId = screening.getScreeningId();
        long showTime = FIXED_DATETIME.minusHours(3).toEpochSecond(OFFSET);
        long movieId = screening.getMovie().getMovieId();
        long auditoriumId = screening.getAuditorium().getAuditoriumId();

        ScreeningDto screeningDto = ScreeningDto.builder()
                .showTime(showTime)
                .movieId(movieId)
                .auditoriumId(auditoriumId)
                .build();

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/screening/{screeingId}", screeningId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(screeningDto)));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.screeningId", is(screeningId), Long.class))
                .andExpect(jsonPath("$.movieId", is(screeningDto.getMovieId()), Long.class))
                .andExpect(jsonPath("$.auditoriumId", is(screeningDto.getAuditoriumId()), Long.class))
                .andExpect(jsonPath("$.showTime", is(screeningDto.getShowTime()), Long.class));
    }

    @Test
    @DisplayName("delete Screening by Id")
    void givenScreeningId_whenDeleteScreening_thenReturnNoContentResponse() throws Exception {
        //given
        Screening screening = this.repository.save(this.screening);
        Long screeningId = screening.getScreeningId();

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/screening/{screeningId}", screeningId));

        //then
        response.andExpect(status().isNoContent())
                .andDo(print());
    }
}
