package com.example.booking.auditorium;

import com.example.booking.config.AppConfig;
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

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AuditoriumController.class, AppConfig.class})
class AuditoriumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditoriumService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelMapper modelMapper;

    private Auditorium auditorium;

    @BeforeEach
    void setUp(){

        this.auditorium = Auditorium.builder()
                .auditoriumId(1L)
                .numberOfSeats(100)
                .build();
    }

    @Test
    @DisplayName("get auditorium")
    void given_whenGetAuditoriums_thenReturnListOfAuditoriumDto() throws Exception {

        //given
        given(this.service.getAuditoriums()).willReturn(List.of(this.auditorium));

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auditorium"));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    @DisplayName("get auditorium by Id")
    void givenAuditoriumId_whenGetAuditoriumById_thenReturnAuditoriumDto() throws Exception {

        //given
        long auditoriumId = 1L;
        given(this.service.getAuditoriumById(auditoriumId)).willReturn(this.auditorium);

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/auditorium/{auditoriumId}", auditoriumId));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.auditoriumId", is(this.auditorium.getAuditoriumId()), Long.class))
                .andExpect(jsonPath("$.numberOfSeats", is(this.auditorium.getNumberOfSeats())));
    }

    @Test
    @DisplayName("Post auditorium")
    void givenAuditoriumDto_whenAddAuditorium_thenReturnAuditoriumDto() throws Exception {
        //given
        AuditoriumDto auditoriumDto = AuditoriumDto.builder()
                .auditoriumId(1L)
                .numberOfSeats(100)
                .build();

        given(this.service.addNewAuditorium(ArgumentMatchers.any(Auditorium.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auditorium")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(auditoriumDto)));


        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.auditoriumId", is(this.auditorium.getAuditoriumId()), Long.class))
                .andExpect(jsonPath("$.numberOfSeats", is(this.auditorium.getNumberOfSeats())));
    }

    @Test
    @DisplayName("delete auditorium")
    void givenAuditoriumId_whenRemoveAuditorium_thenReturnNoContent() throws Exception {
        //given
        long auditoriumId = 1L;
        willDoNothing().given(this.service).deleteAuditorium(auditoriumId);

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/auditorium/{auditoriumId}", auditoriumId));

        //then
        response.andExpect(status().isNoContent())
                .andDo(print());

    }

    @Test
    @DisplayName("update auditorium")
    void givenAuditoriumIdAuditoriumDto_whenUpdateAuditorium_returnAuditoriumDto() throws Exception {

        //given
        long auditoriumId = 1L;
        int numberOfSeats = 100;

        AuditoriumDto auditoriumDto = AuditoriumDto.builder()
                .auditoriumId(auditoriumId)
                .numberOfSeats(numberOfSeats)
                .build();

        given(this.service.updateAuditorium(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(AuditoriumDto.class)))
                .willAnswer((invocation) -> this.modelMapper.map(invocation.getArgument(1), Auditorium.class));

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/auditorium/{auditoriumId}", auditoriumId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(auditoriumDto)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.auditoriumId", is(auditoriumId), Long.class))
                .andExpect(jsonPath("$.numberOfSeats", is(numberOfSeats)));
    }

}