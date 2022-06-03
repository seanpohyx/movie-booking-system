package com.example.booking.integration;

import com.example.booking.auditorium.Auditorium;
import com.example.booking.auditorium.AuditoriumRepository;
import com.example.booking.seat.Seat;
import com.example.booking.seat.SeatId;
import com.example.booking.seat.SeatRepository;
import com.example.booking.seatAuditorium.*;
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

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class SeatAuditoriumControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SeatAuditoriumRepository repository;

    @Autowired
    private AuditoriumRepository auditoriumRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private SeatAuditorium seatAuditorium;

    @BeforeEach
    void setUp(){

        this.repository.deleteAll();
//        this.seatRepository.deleteAll();
//        this.auditoriumRepository.deleteAll();

        Seat seat = this.seatRepository.save(Seat.builder()
                .seatId(SeatId.builder()
                        .rowNumber("Z")
                        .seatNumber(1)
                        .build())
                .cost(new BigDecimal(10.10d))
                .build());

        Auditorium auditorium = this.auditoriumRepository.save(Auditorium.builder()
                .numberOfSeats(500)
                .build());

        this.seatAuditorium = SeatAuditorium.builder()
                .id(SeatAuditoriumCK.builder()
                        .auditoriumId(auditorium.getAuditoriumId())
                        .seatId(seat.getSeatId())
                        .build())
                .seat(seat)
                .auditorium(auditorium)
                .build();
    }

    @Test
    @DisplayName("Get SeatAuditorium")
    void given_whenGetSeatAuditoriums_thenReturnListOfSeatAuditoriumDto() throws Exception {
        //given
        this.repository.save(this.seatAuditorium);
        List<SeatAuditorium> seatAuditoriumList = this.repository.findAll();

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/seatAuditorium"));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(seatAuditoriumList.size())));

    }

    @Test
    @DisplayName("Get SeatAuditorium by Id")
    void givenRowIdSeatNumberAuditoriumId_whengetSeatAuditorium_thenReturnSeatAuditoriumDto() throws Exception {
        //given
        SeatAuditorium seatAuditorium = this.repository.save(this.seatAuditorium);
        Long auditoriumId = seatAuditorium.getAuditorium().getAuditoriumId();
        String rowId = seatAuditorium.getSeat().getSeatId().getRowNumber();
        Integer seatNumber = seatAuditorium.getSeat().getSeatId().getSeatNumber();

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/seatAuditorium/{rowId}/{seatNumber}/{auditoriumId}", rowId, seatNumber, auditoriumId));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.auditoriumId", is(auditoriumId), Long.class))
                .andExpect(jsonPath("$.seatNumber", is(seatNumber)))
                .andExpect(jsonPath("$.rowNumber", is(rowId)));
    }

    @Test
    @DisplayName("Post SeatAuditorium")
    void givenScreeningDto_whenAddSeatAuditorium_thenReturnSeatAuditoriumDto() throws Exception {
        //given

        Seat seat = this.seatRepository.save(Seat.builder()
                .seatId(SeatId.builder()
                        .rowNumber("Y")
                        .seatNumber(2)
                        .build())
                .cost(new BigDecimal(10.10d))
                .build());

        Auditorium auditorium = this.auditoriumRepository.save(Auditorium.builder()
                .numberOfSeats(250)
                .build());

        long auditoriumId = auditorium.getAuditoriumId();
        String rowNumber = seat.getSeatId().getRowNumber();
        Integer seatNumber = seat.getSeatId().getSeatNumber();

        SeatAuditoriumDto seatAuditoriumDto = SeatAuditoriumDto.builder()
                .auditoriumId(auditoriumId)
                .rowNumber(rowNumber)
                .seatNumber(seatNumber)
                .build();

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/seatAuditorium")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(seatAuditoriumDto)));

        //then
        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.auditoriumId", is(auditoriumId), Long.class))
                .andExpect(jsonPath("$.seatNumber", is(seatNumber)))
                .andExpect(jsonPath("$.rowNumber", is(rowNumber)));

    }

    @Test
    @DisplayName("Delete SeatAuditorium")
    void givenRowIdSeatNumberAuditoriumId_whenDeleteSeatAuditorum_thenReturnNoContent() throws Exception {
        //given
        SeatAuditorium seatAuditorium = this.repository.save(this.seatAuditorium);
        Long auditoriumId = seatAuditorium.getAuditorium().getAuditoriumId();
        String rowId = seatAuditorium.getSeat().getSeatId().getRowNumber();
        Integer seatNumber = seatAuditorium.getSeat().getSeatId().getSeatNumber();

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/seatAuditorium/{rowId}/{seatNumber}/{auditoriumId}", rowId, seatNumber, auditoriumId));

        //then
        response.andExpect(status().isNoContent())
                .andDo(print());

    }
}
