package com.example.booking.seatBooking;

import com.example.booking.account.Account;
import com.example.booking.auditorium.Auditorium;
import com.example.booking.movie.Movie;
import com.example.booking.screening.Screening;
import com.example.booking.seat.Seat;
import com.example.booking.seat.SeatId;
import com.example.booking.seatAuditorium.SeatAuditorium;
import com.example.booking.seatAuditorium.SeatAuditoriumCK;
import com.example.booking.config.AppConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {SeatBookingController.class, AppConfig.class})
class SeatBookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SeatBookingService service;

    @Autowired
    private ObjectMapper objectMapper;

    private SeatBooking seatBooking;
    private SeatId seatId;
    private Seat seat;
    private Auditorium auditorium;
    private SeatAuditorium seatAuditorium;
    private Screening screening;
    private Account account;

    @BeforeEach
    void setUp(){

        long bookedTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC.of("+08:00"));

        this.seatId = SeatId.builder()
                .rowNumber("A")
                .seatNumber(1)
                .build();

        this.seat = Seat.builder()
                .seatId(this.seatId)
                .cost(new BigDecimal(10.10d))
                .build();

        this.auditorium = Auditorium.builder()
                .auditoriumId(1L)
                .numberOfSeats(10)
                .build();

        this.seatAuditorium = SeatAuditorium.builder()
                .id(SeatAuditoriumCK.builder()
                        .auditoriumId(1L)
                        .seatId(this.seatId)
                        .build())
                .seat(this.seat)
                .auditorium(this.auditorium)
                .build();

        this.screening = Screening.builder()
                .screeningId(1L)
                .movie(Movie.builder()
                        .movieId(1L)
                        .build())
                .auditorium(this.auditorium)
                .build();

        this.account = Account.builder()
                .accountId(1L)
                .email("test@gmail.com")
                .build();


        this.seatBooking = SeatBooking.builder()
                .seatBookingId(1L)
                .bookedTime(bookedTime)
                .seatAuditorium(this.seatAuditorium)
                .screening(this.screening)
                .account(this.account)
                .build();
    }

    @Test
    @DisplayName("Get seatbooking")
    void given_whenGetSeatBookings_thenReturnListOfSeatBookingDto() throws Exception {
        //given
        given(this.service.getSeatBookings()).willReturn(List.of(this.seatBooking));

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
        long bookingId = 1L;

        given(this.service.getSeatBooking(bookingId)).willReturn(this.seatBooking);

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
                .accountId(1L)
                .auditoriumId(1L)
                .bookedTime(bookedTime)
                .seatNumber(1)
                .rowNumber("A")
                .screeningId(1L)
                .build();

        given(this.service.addSeatBooking(ArgumentMatchers.any(SeatBookingDto.class)))
                .willAnswer((invocation) -> {
                    SeatBookingDto output = (SeatBookingDto) invocation.getArgument(0);
                    SeatBooking newSeatbooking = this.seatBooking;
                    newSeatbooking.setBookedTime(output.getBookedTime());
                    return newSeatbooking;
                });

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/seatbooking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(seatBookingDto)));

        //then
        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.seatBookingId", is(this.seatBooking.getSeatBookingId()), Long.class))
                .andExpect(jsonPath("$.bookedTime", is(bookedTime), Long.class))
                .andExpect(jsonPath("$.auditoriumId", is(this.seatBooking.getSeatAuditorium().getAuditorium().getAuditoriumId()), Long.class))
                .andExpect(jsonPath("$.seatNumber", is(this.seatBooking.getSeatAuditorium().getSeat().getSeatId().getSeatNumber())))
                .andExpect(jsonPath("$.rowNumber", is(this.seatBooking.getSeatAuditorium().getSeat().getSeatId().getRowNumber())))
                .andExpect(jsonPath("$.accountId", is(this.seatBooking.getAccount().getAccountId()), Long.class))
                .andExpect(jsonPath("$.screeningId", is(this.seatBooking.getScreening().getScreeningId()), Long.class));

    }

    @Test
    @DisplayName("Delete seatbooking by Id")
    void givenBookingId_whenDeleteSeatBooking_thenReturnNoContent() throws Exception {
        //given
        long bookingId = 1L;

        willDoNothing().given(this.service).deleteSeatBooking(bookingId);

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
        long bookingId = 1L;
        long bookedTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC.of("+08:00"));
        long accountId = 2L;
        long auditoriumId = 2L;
        long screeningId = 2L;
        String rowNumber = "A";
        int seatNumber = 2;
        SeatBookingDto seatBookingDto = SeatBookingDto.builder()
                .accountId(accountId)
                .auditoriumId(auditoriumId)
                .bookedTime(bookedTime)
                .seatNumber(seatNumber)
                .rowNumber(rowNumber)
                .screeningId(screeningId)
                .build();


        given(this.service.updateSeatBooking(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(SeatBookingDto.class)))
                .willAnswer((invocation) -> {

                    SeatBookingDto output = (SeatBookingDto) invocation.getArgument(1);
                    long id = (Long) invocation.getArgument(0);
                    return SeatBooking.builder()
                            .seatBookingId(id)
                            .bookedTime(output.getBookedTime())
                            .seatAuditorium(SeatAuditorium.builder()
                                    .auditorium(Auditorium.builder()
                                            .auditoriumId(output.getAuditoriumId())
                                            .build())
                                    .seat(Seat.builder()
                                            .seatId(SeatId.builder()
                                                    .seatNumber(output.getSeatNumber())
                                                    .rowNumber(output.getRowNumber())
                                                    .build())
                                            .build())
                                    .build())
                            .screening(Screening.builder()
                                    .screeningId(output.getScreeningId())
                                    .build())
                            .account(Account.builder()
                                    .accountId(output.getAccountId())
                                    .build())
                            .build();
                });
        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/seatbooking/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(seatBookingDto)));

        System.out.println(response);
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