package com.example.booking.integration;

import com.example.booking.seat.Seat;
import com.example.booking.seat.SeatDto;
import com.example.booking.seat.SeatId;
import com.example.booking.seat.SeatRepository;
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
public class SeatControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SeatRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private Seat seat;

    @BeforeEach
    void setUp() {

        this.seat = Seat.builder()
                .seatId(SeatId.builder()
                        .rowNumber("Z")
                        .seatNumber(1)
                        .build())
                .cost(new BigDecimal(10.10d))
                .build();
    }

    @Test
    @DisplayName("Get seats")
    void given_whenGetSeats_thenReturnListOfSeats() throws Exception {
        //given
        List<Seat> seats = this.repository.findAll();

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/seat"));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(seats.size())));
    }

    @Test
    @DisplayName("Get seat with Id")
    void givenRowIdSeatNumber_whenGetSeat_thenReturnSeat() throws Exception {
        //given
        List<Seat> seats = this.repository.findAll();
        String rowId = seats.get(0).getSeatId().getRowNumber();
        int seatNumber = seats.get(0).getSeatId().getSeatNumber();

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/seat/{rowId}/{seatNumber}", rowId, seatNumber));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.rowNumber", is(seats.get(0).getSeatId().getRowNumber())))
                .andExpect(jsonPath("$.seatNumber", is(seats.get(0).getSeatId().getSeatNumber())))
                .andExpect(jsonPath("$.cost", is(seats.get(0).getCost().setScale(1)), BigDecimal.class));
    }

    @Test
    @DisplayName("Post seat")
    void givenSeatDto_whenAddSeat_thenReturnSeatDto() throws Exception {
        //given
        SeatDto seatDto = SeatDto.builder()
                .rowNumber("Z")
                .seatNumber(10)
                .cost(new BigDecimal(10.10d))
                .build();

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/seat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(seatDto)));

        //then
        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.rowNumber", is(seatDto.getRowNumber())))
                .andExpect(jsonPath("$.seatNumber", is(seatDto.getSeatNumber())))
                .andExpect(jsonPath("$.cost", is(seatDto.getCost())));

    }

    @Test
    @DisplayName("Delete seat")
    void givenRowIdSeatNumber_whenDeleteSeat_thenReturnNoContent() throws Exception {

        //given
        Seat input = this.repository.save(Seat.builder()
                .seatId(SeatId.builder()
                        .rowNumber("X")
                        .seatNumber(10)
                        .build())
                .cost(new BigDecimal(10.10d))
                .build());
        String rowId = input.getSeatId().getRowNumber();
        int seatNumber = input.getSeatId().getSeatNumber();

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/seat/{rowId}/{seatNumber}", rowId, seatNumber));

        //then
        response.andExpect(status().isNoContent())
                .andDo(print());

    }

    @Test
    @DisplayName("update seat")
    void givenRowIdSeatNumberSeatDto_whenUpdateSeat_thenReturnNoContent() throws Exception {
        //given
        List<Seat> seats = this.repository.findAll();
        String rowId = seats.get(0).getSeatId().getRowNumber();
        int seatId = seats.get(0).getSeatId().getSeatNumber();

        BigDecimal newCost = new BigDecimal(100.10d);

        SeatDto seatDto = SeatDto.builder()
                .seatNumber(seatId)
                .rowNumber(rowId)
                .cost(newCost)
                .build();

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/seat/{rowId}/{seatNumber}", rowId, seatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(seatDto)));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.rowNumber", is(rowId)))
                .andExpect(jsonPath("$.seatNumber", is(seatId)))
                .andExpect(jsonPath("$.cost", is(newCost)));
    }
}
