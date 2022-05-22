package com.example.booking.screening;

import com.example.booking.auditorium.Auditorium;
import com.example.booking.movie.Movie;
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

@WebMvcTest(controllers = {ScreeningController.class, AppConfig.class})
class ScreeningControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScreeningService service;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private Screening screening;

    private ZoneOffset OFFSET = ZoneOffset.UTC.of("+08:00");
    private LocalDateTime FIXED_DATETIME = LocalDateTime.of(2022, Month.MARCH, 01, 8, 00, 00);

    @BeforeEach
    void setUp(){
        long epochTimeNow = LocalDateTime.now().toEpochSecond(OFFSET);
        long showTime = FIXED_DATETIME.toEpochSecond(OFFSET);

        Auditorium auditorium = Auditorium.builder()
                .auditoriumId(1L)
                .numberOfSeats(10)
                .build();

        Movie movie = Movie.builder()
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
                .movie(movie)
                .auditorium(auditorium)
                .showTime(showTime)
                .build();
    }

    @Test
    @DisplayName("Get Screening")
    void given_whenGetScreening_thenReturnListOfScreeningDto() throws Exception {
        //given
        given(this.service.getScreenings()).willReturn(List.of(this.screening));

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/screening"));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(1)));

    }

    @Test
    @DisplayName("Get Screening by Id")
    void givenScreeningId_whenGetScreeningById_thenReturnScreeningDto() throws Exception {
        //given
        Long screeningId = 1L;
        ScreeningDto screeningDto = this.modelMapper.map(this.screening, ScreeningDto.class);
        given(this.service.getScreeningById(screeningId)).willReturn(this.screening);

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/screening/{screeningId}", screeningId));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.screeningId", is(screeningDto.getScreeningId()), Long.class))
                .andExpect(jsonPath("$.movieId", is(screeningDto.getMovieId()), Long.class))
                .andExpect(jsonPath("$.auditoriumId", is(screeningDto.getScreeningId()), Long.class))
                .andExpect(jsonPath("$.showTime", is(screeningDto.getShowTime()), Long.class));
    }

    @Test
    @DisplayName("Post Screening")
    void givenScreeningDto_whenAddScreening_thenReturnScreeningDto() throws Exception {
        //given
        long showTime = FIXED_DATETIME.toEpochSecond(OFFSET);
        long movieId = 1L;
        long auditoriumId = 1L;

        ScreeningDto screeningDto = ScreeningDto.builder()
                .showTime(showTime)
                .movieId(movieId)
                .auditoriumId(auditoriumId)
                .build();

        given(this.service.addScreening(ArgumentMatchers.any(ScreeningDto.class)))
                .willAnswer((invocation) -> {
                    Screening screening = this.modelMapper.map(invocation.getArgument(0), Screening.class);
                    screening.setScreeningId(1L);
                    return screening;
                });

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/screening")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screeningDto)));

        //then
        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.screeningId", is(1L), Long.class))
                .andExpect(jsonPath("$.movieId", is(screeningDto.getMovieId()), Long.class))
                .andExpect(jsonPath("$.auditoriumId", is(screeningDto.getAuditoriumId()), Long.class))
                .andExpect(jsonPath("$.showTime", is(screeningDto.getShowTime()), Long.class));

    }

    @Test
    @DisplayName("Update screening by Id")
    void givenScreeningIdShowTimeMovieIdAuditoriumId_whenUpdateScreening_thenReturnScreeningDto() throws Exception {
        //given
        long screeningId = 1L;
        long showTime = FIXED_DATETIME.toEpochSecond(OFFSET);
        long movieId = 2L; //new values
        long auditoriumId = 2L; //new values

        ScreeningDto screeningDto = ScreeningDto.builder()
                .showTime(showTime)
                .movieId(movieId)
                .auditoriumId(auditoriumId)
                .build();

        given(this.service.updateScreening(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(ScreeningDto.class)))
                .willAnswer((invocation) -> {
                    Screening output = this.modelMapper.map(invocation.getArgument(1), Screening.class);
                    output.setScreeningId(invocation.getArgument(0));
                    return output;
                });

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
        Long screeningId = 1L;

        willDoNothing().given(this.service).deleteScreening(screeningId);

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/screening/{screeningId}", screeningId));

        //then
        response.andExpect(status().isNoContent())
                .andDo(print());
    }



}