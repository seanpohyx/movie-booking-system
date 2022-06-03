package com.example.booking.integration;

import com.example.booking.account.Account;
import com.example.booking.account.AccountRepository;
import com.example.booking.movie.Movie;
import com.example.booking.movie.MovieRepository;
import com.example.booking.screening.Screening;
import com.example.booking.screening.ScreeningRepository;
import com.example.booking.seatAuditorium.SeatAuditorium;
import com.example.booking.seatAuditorium.SeatAuditoriumRepository;
import com.example.booking.seatBooking.SeatBooking;
import com.example.booking.seatBooking.SeatBookingDto;
import com.example.booking.seatBooking.SeatBookingRepository;
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

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class SeatBookingIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SeatBookingRepository repository;

    @Autowired
    private ScreeningRepository screeningRepository;

    @Autowired
    private SeatAuditoriumRepository seatAuditoriumRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private SeatBooking seatBooking;
    private SeatAuditorium seatAuditorium;
    private Screening screening;
    private Account account;
    private Movie movie;
    private ZoneOffset OFFSET = ZoneOffset.UTC.of("+08:00");
    private LocalDateTime FIXED_DATETIME = LocalDateTime.of(2022, Month.MARCH, 01, 8, 00, 00);

    @BeforeEach
    void setUp(){

        this.screeningRepository.deleteAll();
        this.accountRepository.deleteAll();

        long bookedTime = LocalDateTime.now().toEpochSecond(OFFSET);
        long showTime = FIXED_DATETIME.toEpochSecond(OFFSET);

        this.seatAuditorium = this.seatAuditoriumRepository.findAll().get(0);

        this.movie = this.movieRepository.findAll().get(0);

        this.screening = this.screeningRepository.save(Screening.builder()
                .screeningId(1L)
                .movie(movie)
                .auditorium(seatAuditorium.getAuditorium())
                .showTime(showTime)
                .build());

        this.account = this.accountRepository.save(Account.builder()
                .accountId(1L)
                .name("tester")
                .password("123123123")
                .email("test@gmail.com")
                .build());

        this.seatBooking = SeatBooking.builder()
                .bookedTime(bookedTime)
                .seatAuditorium(this.seatAuditorium)
                .screening(this.screening)
                .account(this.account)
                .build();
    }

    @AfterEach
    void cleanUp(){
        this.repository.deleteAll();
    }

    @Test
    @DisplayName("Get seatbooking")
    void given_whenGetSeatBookings_thenReturnListOfSeatBookingDto() throws Exception {
        //given
        this.repository.save(seatBooking);

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/seatbooking"));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(1)));

    }

    @Test
    @DisplayName("Get seatBooking with booking id")
    void givenBookingId_whenGetSeatBooking_thenReturnSeatBookingDto() throws Exception {
        //given
        SeatBooking seatBooking = this.repository.save(this.seatBooking);
        long bookingId = seatBooking.getSeatBookingId();

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/seatbooking/{bookingId}", bookingId));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.seatBookingId", is(this.seatBooking.getSeatBookingId()), Long.class))
                .andExpect(jsonPath("$.bookedTime", is(this.seatBooking.getBookedTime()), Long.class))
                .andExpect(jsonPath("$.auditoriumId", is(this.seatBooking.getSeatAuditorium().getAuditorium().getAuditoriumId()), Long.class))
                .andExpect(jsonPath("$.seatNumber", is(this.seatBooking.getSeatAuditorium().getSeat().getSeatId().getSeatNumber())))
                .andExpect(jsonPath("$.rowNumber", is(this.seatBooking.getSeatAuditorium().getSeat().getSeatId().getRowNumber())))
                .andExpect(jsonPath("$.accountId", is(this.seatBooking.getAccount().getAccountId()), Long.class))
                .andExpect(jsonPath("$.screeningId", is(this.seatBooking.getScreening().getScreeningId()), Long.class));

    }

    @Test
    @DisplayName("Post SeatBooking")
    void givenSeatBookingDto_whenAddSeatBooking_thenReturnSeatBookingDto() throws Exception {
        //given

        long bookedTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC.of("+08:00"));
        SeatBookingDto seatBookingDto = SeatBookingDto.builder()
                .accountId(this.account.getAccountId())
                .auditoriumId(this.seatAuditorium.getAuditorium().getAuditoriumId())
                .bookedTime(bookedTime)
                .seatNumber(this.seatAuditorium.getSeat().getSeatId().getSeatNumber())
                .rowNumber(this.seatAuditorium.getSeat().getSeatId().getRowNumber())
                .screeningId(this.screening.getScreeningId())
                .build();

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/seatbooking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(seatBookingDto)));

        //then
        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.bookedTime", is(bookedTime), Long.class))
                .andExpect(jsonPath("$.auditoriumId", is(this.seatAuditorium.getAuditorium().getAuditoriumId()), Long.class))
                .andExpect(jsonPath("$.seatNumber", is(this.seatAuditorium.getSeat().getSeatId().getSeatNumber())))
                .andExpect(jsonPath("$.rowNumber", is(this.seatAuditorium.getSeat().getSeatId().getRowNumber())))
                .andExpect(jsonPath("$.accountId", is(this.account.getAccountId()), Long.class))
                .andExpect(jsonPath("$.screeningId", is(this.screening.getScreeningId()), Long.class));

    }

    @Test
    @DisplayName("Delete seatbooking by Id")
    void givenBookingId_whenDeleteSeatBooking_thenReturnNoContent() throws Exception {
        //given
        SeatBooking seatBooking = this.repository.save(this.seatBooking);
        long bookingId = seatBooking.getSeatBookingId();

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/seatbooking/{bookingId}", bookingId));

        //then
        response.andExpect(status().isNoContent())
                .andDo(print());

    }

    @Test
    @DisplayName("Update seatbooking")
    void givenBookingIdSeatBookingDto_whenUpdateSeatBooking_thenReturnSeatBookingDto() throws Exception {
        //given
        SeatBooking seatBooking = this.repository.save(this.seatBooking);
        long bookingId = seatBooking.getSeatBookingId();
        long bookedTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC.of("+08:00")); //new
        long accountId = seatBooking.getAccount().getAccountId();
        long auditoriumId = seatBooking.getSeatAuditorium().getAuditorium().getAuditoriumId();
        long screeningId = seatBooking.getScreening().getScreeningId();
        String rowNumber = seatBooking.getSeatAuditorium().getSeat().getSeatId().getRowNumber();
        int seatNumber = seatBooking.getSeatAuditorium().getSeat().getSeatId().getSeatNumber();

        SeatBookingDto seatBookingDto = SeatBookingDto.builder()
                .accountId(accountId)
                .auditoriumId(auditoriumId)
                .bookedTime(bookedTime)
                .seatNumber(seatNumber)
                .rowNumber(rowNumber)
                .screeningId(screeningId)
                .build();
        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/seatbooking/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(seatBookingDto)));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.seatBookingId", is(bookingId), Long.class))
                .andExpect(jsonPath("$.bookedTime", is(bookedTime), Long.class))
                .andExpect(jsonPath("$.auditoriumId", is(auditoriumId), Long.class))
                .andExpect(jsonPath("$.seatNumber", is(seatNumber)))
                .andExpect(jsonPath("$.rowNumber", is(rowNumber)))
                .andExpect(jsonPath("$.accountId", is(accountId), Long.class))
                .andExpect(jsonPath("$.screeningId", is(screeningId), Long.class));
    }

}
