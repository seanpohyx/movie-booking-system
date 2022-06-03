package com.example.booking.integration;

import com.example.booking.movie.Movie;
import com.example.booking.movie.MovieDto;
import com.example.booking.movie.MovieRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class MovieControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private Movie movie;

    @BeforeEach
    void setUp(){

        long epochTimeNow = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC.of("+08:00"));
        this.movie = Movie.builder()
                .createdDateTime(epochTimeNow)
                .duration(60 + 60 + 52)
                .startDate(LocalDate.of(2022, Month.MARCH, 02))
                .endDate(LocalDate.of(2040, Month.MAY, 10))
                .title("The batman")
                .casts("Robert Pattison")
                .updatedDateTime(epochTimeNow)
                .description("When the Riddler, a sadistic serial killer, begins murdering key political " +
                        "figures in Gotham, Batman is forced to investigate the city's hidden corruption " +
                        "and question his family's involvement.")
                .build();
    }

    @Test
    @DisplayName("Get movies")
    void given_whenGetMovies_thenReturnListOfMovies() throws Exception {

        //given
        this.repository.save(this.movie);
        List<Movie> movies = this.repository.findAll();

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/movie"));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(movies.size())));

    }

    @Test
    @DisplayName("Get movie with Id")
    void givenMovieId_whenGetMovie_thenReturnMovie() throws Exception {
        //given
        Movie movie = this.repository.save(this.movie);
        Long movieId = movie.getMovieId();

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/movie/{id}", movieId));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.movieId", is(this.movie.getMovieId()), Long.class))
                .andExpect(jsonPath("$.title", is(this.movie.getTitle())))
                .andExpect(jsonPath("$.description", is(this.movie.getDescription())))
                .andExpect(jsonPath("$.duration", is(this.movie.getDuration())))
                .andExpect(jsonPath("$.casts", is(this.movie.getCasts())))
                .andExpect(jsonPath("$.startDate", is(this.movie.getStartDate().toString())))
                .andExpect(jsonPath("$.endDate", is(this.movie.getEndDate().toString())))
                .andExpect(jsonPath("$.createdDate", is(this.movie.getCreatedDateTime().intValue())))
                .andExpect(jsonPath("$.updatedTime", is(this.movie.getUpdatedDateTime().intValue())));
    }

    @Test
    @DisplayName("Get movie with Id - throws exception MovieIsNotFound")
    void givenMovieId_whenGetMovie_thenReturnMovieIsNotFound() throws Exception {
        //given
        Long movieId = 100L;

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/movie/{id}", movieId));

        //then
        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("Get now showing")
    void given_whenGetLatest_thenReturnListOfMovies() throws Exception {
        //given
        this.repository.save(this.movie);

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/movie/nowShowing"));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(2)));

    }

    @Test
    @DisplayName("Post movie")
    void givenMovieDto_whenAddMovie_thenReturnMovieDto() throws Exception {
        //given
        MovieDto movieDto = MovieDto.builder()
                .duration(60 + 60 + 52)
                .startDate(LocalDate.of(2022, Month.MARCH, 02))
                .endDate(LocalDate.of(2022, Month.MAY, 10))
                .title("The batman")
                .casts("Robert Pattison")
                .description("When the Riddler, a sadistic serial killer, begins murdering key political figures in Gotham, Batman is forced to investigate the city's hidden corruption and question his family's involvement.")
                .build();

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/movie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDto)));

        //then
        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.title", is(movieDto.getTitle())))
                .andExpect(jsonPath("$.description", is(movieDto.getDescription())))
                .andExpect(jsonPath("$.duration", is(movieDto.getDuration().intValue())))
                .andExpect(jsonPath("$.casts", is(movieDto.getCasts())))
                .andExpect(jsonPath("$.startDate", is(movieDto.getStartDate().toString())))
                .andExpect(jsonPath("$.endDate", is(movieDto.getEndDate().toString())));

    }
    @Test
    @DisplayName("Delete movie")
    void givenMovieId_whenDeleteMovie_thenReturnNoContent() throws Exception {
        //given
        Movie movie = this.repository.save(this.movie);
        Long movieId = movie.getMovieId();

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/movie/{movieId}", movieId));

        //then
        response.andExpect(status().isNoContent())
                .andDo(print());

    }

    @Test
    @DisplayName("Update movie")
    void givenMovieIdMovie_whenUpdateMovie_thenReturnMovieDto() throws Exception {
        //given
        Movie movie = this.repository.save(this.movie);
        Long movieId = movie.getMovieId();
        int duration = 60 + 60 + 6;
        LocalDate startDate = LocalDate.of(2022, Month.MARCH, 02);
        LocalDate endDateTime = LocalDate.of(2022, Month.MAY, 10);
        String title = "Doctor Strange in the Multiverse of Madness";
        String casts = "benedict cumberbatch";
        String description = "Dr Stephen Strange casts a forbidden spell that opens a " +
                "portal to the multiverse. However, a threat emerges that may be too big " +
                "for his team to handle.";

        MovieDto movieDto = MovieDto.builder()
                .duration(duration)
                .startDate(startDate)
                .endDate(endDateTime)
                .title(title)
                .casts(casts)
                .description(description)
                .build();

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/movie/{movieId}", movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(movieDto)));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.title", is(movieDto.getTitle())))
                .andExpect(jsonPath("$.description", is(movieDto.getDescription())))
                .andExpect(jsonPath("$.duration", is(movieDto.getDuration().intValue())))
                .andExpect(jsonPath("$.casts", is(movieDto.getCasts())))
                .andExpect(jsonPath("$.startDate", is(movieDto.getStartDate().toString())))
                .andExpect(jsonPath("$.endDate", is(movieDto.getEndDate().toString())));
    }


}
