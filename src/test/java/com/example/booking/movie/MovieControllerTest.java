package com.example.booking.movie;

import com.example.booking.shared.AppConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {MovieController.class, AppConfig.class})
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService service;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

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
    @DisplayName("Get movies")
    void given_whenGetMovies_thenReturnListOfMovies() throws Exception {

        //given
        given(this.service.getMovies()).willReturn(List.of(this.movie));

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/movie"));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(1)));

    }

    @Test
    @DisplayName("Get movie with Id")
    void givenMovieId_whenGetMovie_thenReturnMovie() throws Exception {
        //given
        Long movieId = 1L;
        given(this.service.getMovieById(movieId)).willReturn(this.movie);

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/movie/{id}", movieId));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.movieId", is(this.movie.getMovieId()), Long.class))
                .andExpect(jsonPath("$.title", is(this.movie.getTitle())))
                .andExpect(jsonPath("$.description", is(this.movie.getDescription())))
                .andExpect(jsonPath("$.duration", is(this.movie.getDuration().intValue())))
                .andExpect(jsonPath("$.casts", is(this.movie.getCasts())))
                .andExpect(jsonPath("$.startDate", is(this.movie.getStartDate().toString())))
                .andExpect(jsonPath("$.endDate", is(this.movie.getEndDate().toString())))
                .andExpect(jsonPath("$.createdDate", is(this.movie.getCreatedDateTime().intValue())))
                .andExpect(jsonPath("$.updatedTime", is(this.movie.getUpdatedDateTime().intValue())));
    }

    @Test
    @DisplayName("Get latest movies")
    void given_whenGetLatest_thenReturnListOfMovies() throws Exception {
        //given

        given(this.service.getNowShowing()).willReturn(List.of(this.movie));

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/movie/latest"));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(1)));

    }

    @Test
    @DisplayName("Post movie")
    void givenMovieDto_whenAddMovie_thenReturnMovieDto() throws Exception {
        //given
        Movie inputMovie = Movie.builder()
                .duration(60 + 60 + 52)
                .startDate(LocalDate.of(2022, Month.MARCH, 02))
                .endDate(LocalDate.of(2022, Month.MAY, 10))
                .title("The batman")
                .casts("Robert Pattison")
                .updatedDateTime(null)
                .createdDateTime(null)
                .description("When the Riddler, a sadistic serial killer, begins murdering key political figures in Gotham, Batman is forced to investigate the city's hidden corruption and question his family's involvement.")
                .build();

        given(this.service.addNewMovie(ArgumentMatchers.any(Movie.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/movie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(this.modelMapper.map(inputMovie, MovieDto.class))));

        //then
        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.title", is(inputMovie.getTitle())))
                .andExpect(jsonPath("$.description", is(inputMovie.getDescription())))
                .andExpect(jsonPath("$.duration", is(inputMovie.getDuration().intValue())))
                .andExpect(jsonPath("$.casts", is(inputMovie.getCasts())))
                .andExpect(jsonPath("$.startDate", is(inputMovie.getStartDate().toString())))
                .andExpect(jsonPath("$.endDate", is(inputMovie.getEndDate().toString())));

    }


    @Test
    @DisplayName("Delete movie")
    void givenMovieId_whenDeleteMovie_thenReturnNoContent() throws Exception {
        //given
        Long movieId = 1L;

        willDoNothing().given(this.service).deleteMovie(movieId);

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
        long movieId = 1L;
        int duration = 60 + 60 + 52;
        LocalDate startDate = LocalDate.of(2022, Month.MARCH, 02);
        LocalDate endDateTime = LocalDate.of(2022, Month.MAY, 10);
        String title = "The batman";
        String casts = "Robert Pattison";
        String description = "When the Riddler, a sadistic serial killer, begins murdering key political figures in Gotham, Batman is forced to investigate the city's hidden corruption and question his family's involvement.";

        MovieDto movieDto = MovieDto.builder()
                .duration(duration)
                .startDate(startDate)
                .endDate(endDateTime)
                .title(title)
                .casts(casts)
                .description(description)
                .build();

        given(this.service.updateMovie(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(MovieDto.class)))
                .willAnswer((invocation) -> {
                    //need to return a movie.class
                    return this.modelMapper.map(invocation.getArgument(1), Movie.class);
                });
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