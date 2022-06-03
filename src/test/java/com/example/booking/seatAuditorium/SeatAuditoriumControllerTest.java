package com.example.booking.seatAuditorium;

import com.example.booking.auditorium.Auditorium;
import com.example.booking.seat.Seat;
import com.example.booking.seat.SeatId;
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

@WebMvcTest(controllers = {SeatAuditoriumController.class, AppConfig.class})
class SeatAuditoriumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SeatAuditoriumService service;

    @Autowired
    private ObjectMapper objectMapper;

    private SeatAuditorium seatAuditorium;

    @BeforeEach
    void setUp(){

        SeatId seatId = SeatId.builder()
                .rowNumber("A")
                .seatNumber(1)
                .build();

        Seat seat = Seat.builder()
                .seatId(seatId)
                .cost(new BigDecimal(10.10d))
                .build();

        Auditorium auditorium = Auditorium.builder()
                .auditoriumId(1L)
                .numberOfSeats(10)
                .build();

        this.seatAuditorium = SeatAuditorium.builder()
                .id(SeatAuditoriumCK.builder()
                        .auditoriumId(1L)
                        .seatId(seatId)
                        .build())
                .seat(seat)
                .auditorium(auditorium)
                .build();
    }

    @Test
    @DisplayName("Get SeatAuditorium")
    void given_whenGetSeatAuditoriums_thenReturnListOfSeatAuditoriumDto() throws Exception {
        //given
        given(this.service.getSeatAuditoriumList()).willReturn(List.of(this.seatAuditorium));

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/seatAuditorium"));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(1)));

    }

    @Test
    @DisplayName("Get SeatAuditorium by Id")
    void givenRowIdSeatNumberAuditoriumId_whengetSeatAuditorium_thenReturnSeatAuditoriumDto() throws Exception {
        //given
        Long auditoriumId = 1L;
        String rowId = "A";
        Integer seatNumber = 1;

        given(this.service.getSeatAuditorium(seatNumber, rowId, auditoriumId)).willReturn(this.seatAuditorium);

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/seatAuditorium/{rowId}/{seatNumber}/{auditoriumId}", rowId, seatNumber, auditoriumId));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.auditoriumId", is(this.seatAuditorium.getAuditorium().getAuditoriumId()), Long.class))
                .andExpect(jsonPath("$.seatNumber", is(this.seatAuditorium.getSeat().getSeatId().getSeatNumber())))
                .andExpect(jsonPath("$.rowNumber", is(this.seatAuditorium.getSeat().getSeatId().getRowNumber())));
    }

    @Test
    @DisplayName("Post SeatAuditorium")
    void givenScreeningDto_whenAddSeatAuditorium_thenReturnSeatAuditoriumDto() throws Exception {
        //given
        long auditoriumId = 1L;
        Integer seatNumber = 1;
        String rowNumber = "A";

        SeatAuditoriumDto seatAuditoriumDto = SeatAuditoriumDto.builder()
                .auditoriumId(auditoriumId)
                .rowNumber(rowNumber)
                .seatNumber(seatNumber)
                .build();

        given(this.service.addSeatAuditorium(ArgumentMatchers.any(SeatAuditoriumDto.class)))
                .willAnswer((invocation) -> {
                    SeatAuditoriumDto output = (SeatAuditoriumDto) invocation.getArgument(0);
                    return SeatAuditorium.builder()
                            .seat(Seat.builder()
                                    .seatId(SeatId.builder()
                                            .rowNumber(output.getRowNumber())
                                            .seatNumber(output.getSeatNumber())
                                            .build())
                                    .build())
                            .auditorium(Auditorium.builder()
                                    .auditoriumId(output.getAuditoriumId())
                                    .build())
                            .build();
                });

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/seatAuditorium")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(seatAuditoriumDto)));

        //then
        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.auditoriumId", is(this.seatAuditorium.getAuditorium().getAuditoriumId()), Long.class))
                .andExpect(jsonPath("$.seatNumber", is(this.seatAuditorium.getSeat().getSeatId().getSeatNumber())))
                .andExpect(jsonPath("$.rowNumber", is(this.seatAuditorium.getSeat().getSeatId().getRowNumber())));

    }

    @Test
    @DisplayName("Delete SeatAuditorium")
    void givenRowIdSeatNumberAuditoriumId_whenDeleteSeatAuditorum_thenReturnNoContent() throws Exception {
        //given
        int seatNumber = 1;
        String rowId = "A";
        long auditoriumId = 1L;

        willDoNothing().given(this.service).deleteSeatAuditorium(rowId, seatNumber, auditoriumId);

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/seatAuditorium/{rowId}/{seatNumber}/{auditoriumId}", rowId, seatNumber, auditoriumId));

        //then
        response.andExpect(status().isNoContent())
                .andDo(print());

    }

}