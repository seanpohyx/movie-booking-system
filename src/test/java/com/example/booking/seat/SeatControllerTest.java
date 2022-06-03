package com.example.booking.seat;

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
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = {SeatController.class, AppConfig.class})
class SeatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SeatService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Seat seat;

    @BeforeEach
    void setUp() {
        this.seat = Seat.builder()
                .seatId(SeatId.builder()
                        .rowNumber("A")
                        .seatNumber(1)
                        .build())
                .cost(new BigDecimal(10.10d))
                .build();
    }

    @Test
    @DisplayName("Get seats")
    void given_whenGetSeats_thenReturnListOfSeats() throws Exception {
        //given
        given(this.service.getSeats()).willReturn(List.of(this.seat));

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/seat"));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    @DisplayName("Get seat with Id")
    void givenRowIdSeatNumber_whenGetSeat_thenReturnSeat() throws Exception {
        //given
        String rowId = "A";
        int seatNumber = 1;

        given(this.service.getSeat(seatNumber, rowId)).willReturn(this.seat);

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/seat/{rowId}/{seatNumber}", rowId, seatNumber));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.rowNumber", is(this.seat.getSeatId().getRowNumber())))
                .andExpect(jsonPath("$.seatNumber", is(this.seat.getSeatId().getSeatNumber())))
                .andExpect(jsonPath("$.cost", is(this.seat.getCost())));
    }

    @Test
    @DisplayName("Post seat")
    void givenSeatDto_whenAddSeat_thenReturnSeatDto() throws Exception {
        //given
        SeatDto seatDto = SeatDto.builder()
                .rowNumber("A")
                .seatNumber(1)
                .cost(new BigDecimal(10.10d))
                .build();

        given(this.service.addNewSeat(ArgumentMatchers.any(Seat.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

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
        String rowId = "A";
        int seatNumber = 1;

        willDoNothing().given(this.service).deleteSeat(rowId, seatNumber);

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
        Integer seatId = 1;
        String rowId = "A";
        BigDecimal newCost = new BigDecimal(100.10d);

        SeatDto seatDto = SeatDto.builder()
                .seatNumber(seatId)
                .rowNumber(rowId)
                .cost(newCost)
                .build();

        given(this.service.updateSeat(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Integer.class), ArgumentMatchers.any(Seat.class)))
                .willAnswer((invocation) -> invocation.getArgument(2));

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