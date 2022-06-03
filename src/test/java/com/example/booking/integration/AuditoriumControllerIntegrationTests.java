package com.example.booking.integration;

import com.example.booking.auditorium.Auditorium;
import com.example.booking.auditorium.AuditoriumDto;
import com.example.booking.auditorium.AuditoriumRepository;
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

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuditoriumControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuditoriumRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private Auditorium auditorium;

    @BeforeEach
    void setUp(){

        this.auditorium = Auditorium.builder()
                .numberOfSeats(500)
                .build();
    }

    @Test
    @DisplayName("get auditorium")
    void given_whenGetAuditoriums_thenReturnListOfAuditoriumDto() throws Exception {

        //given
        List<Auditorium> auditoriums = this.repository.findAll();

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auditorium"));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(auditoriums.size())));
    }

    @Test
    @DisplayName("get auditorium by Id")
    void givenAuditoriumId_whenGetAuditoriumById_thenReturnAuditoriumDto() throws Exception {

        //given
        Auditorium auditorium = this.repository.save(this.auditorium);
        long auditoriumId = auditorium.getAuditoriumId();

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auditorium/{auditoriumId}", auditoriumId));

        //then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.auditoriumId", is(auditorium.getAuditoriumId()), Long.class))
                .andExpect(jsonPath("$.numberOfSeats", is(auditorium.getNumberOfSeats())));
    }

    @Test
    @DisplayName("Post auditorium")
    void givenAuditoriumDto_whenAddAuditorium_thenReturnAuditoriumDto() throws Exception {
        //given
        AuditoriumDto auditoriumDto = AuditoriumDto.builder()
                .numberOfSeats(750)
                .build();

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auditorium")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(auditoriumDto)));


        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.numberOfSeats", is(auditoriumDto.getNumberOfSeats())));
    }

    @Test
    @DisplayName("delete auditorium")
    void givenAuditoriumId_whenRemoveAuditorium_thenReturnNoContent() throws Exception {
        //given
        Auditorium auditorium = this.repository.save(this.auditorium);
        long auditoriumId = auditorium.getAuditoriumId();

        //when
        ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/auditorium/{auditoriumId}", auditoriumId));

        //then
        response.andExpect(status().isNoContent())
                .andDo(print());

    }

    @Test
    @DisplayName("update auditorium")
    void givenAuditoriumIdAuditoriumDto_whenUpdateAuditorium_returnAuditoriumDto() throws Exception {

        //given
        Auditorium auditorium = this.repository.save(this.auditorium);
        long auditoriumId = auditorium.getAuditoriumId();
        int numberOfSeats = 100;

        AuditoriumDto auditoriumDto = AuditoriumDto.builder()
                .auditoriumId(auditoriumId)
                .numberOfSeats(numberOfSeats)
                .build();

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
