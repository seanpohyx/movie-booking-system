package com.example.booking.auditorium;

import com.example.booking.exception.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuditoriumServiceTest {

    @Mock
    private AuditoriumRepository repository;

    @InjectMocks
    private AuditoriumService underTest;

    @Test
    @DisplayName("Get all Auditoriums")
    void given_whenGetAuditorium_thenReturnListOfAuditoriums(){
        //given
        Auditorium auditorium = Auditorium.builder()
                .auditoriumId(1L)
                .build();
        given(this.repository.findAll()).willReturn(List.of(auditorium));

        //
        List<Auditorium> testAuditoriums = this.underTest.getAuditoriums();

        //then
        verify(this.repository, times(1)).findAll();
        assertThat(testAuditoriums.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("get Auditorium By Id throws Exception")
    void givenId_whenGetAuditoriumById_thenReturnAuditorium() {
        //given
        long id = 1l;
        Auditorium auditorium = Auditorium.builder()
                .auditoriumId(1L)
                .build();

        given(this.repository.findById(id)).willReturn(Optional.of(auditorium));

        //when
        Auditorium testAuditorium = this.underTest.getAuditoriumById(id);

        //then
        assertThat(testAuditorium).isEqualTo(auditorium);
    }

    @Test
    @DisplayName("Add new Auditorium")
    void givenAuditorium_whenAddNewAuditorium_thenReturnAuditorium(){
        //given
        Auditorium auditorium = Auditorium.builder()
                .auditoriumId(1L)
                .numberOfSeats(10)
                .build();

        given(this.repository.save(auditorium)).willReturn(auditorium);

        //when
        Auditorium testAuditorium = this.underTest.addNewAuditorium(auditorium);

        //then
        assertThat(testAuditorium).isEqualTo(auditorium);


    }

    @Test
    @DisplayName("Delete Auditorium by Id")
    void givenId_whenDeleteAuditorium_thenDoNothing() {
        //given
        long id = 1L;

        given(this.repository.existsById(id)).willReturn(true);
        willDoNothing().given(this.repository).deleteById(id);

        //when
        this.underTest.deleteAuditorium(id);

        //then
        verify(this.repository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("get Auditorium By Id throws Exception")
    void givenId_whenGetAuditoriumById_thenThrowsException() {
        //given
        long id = 1l;

        given(this.repository.findById(id)).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> this.underTest.getAuditoriumById(id))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Auditorium with Id " + id + " does not exists");
    }

    @Test
    @DisplayName("Delete Auditorium by Id throws Exception")
    void givenId_whenDeleteAuditorium_thenThrowsException() {
        //given
        long id = 1L;

        //when
        given(this.repository.existsById(id)).willReturn(false);

        //then
        assertThatThrownBy(() -> this.underTest.deleteAuditorium(id))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Auditorium with Id " + id + " does not exists");
    }

    @Test
    @DisplayName("Update Auditorium throws exceptions")
    void givenIdNumberOfSeats_whenUpdateAuditorium_thenDoNothing() {
        //given
        long id = 1L;
        int numberOfSeats = 100;

        Auditorium auditorium = Auditorium.builder()
                .auditoriumId(id)
                .numberOfSeats(10)
                .build();

        given(this.repository.findById(id)).willReturn(Optional.of(auditorium));

        //when
        this.underTest.updateAuditorium(id, numberOfSeats);

        //then
        ArgumentCaptor<Auditorium> auditoriumArgumentCaptor =
                ArgumentCaptor.forClass(Auditorium.class);

        verify(this.repository).save(auditoriumArgumentCaptor.capture());

        Auditorium testAuditorium = auditoriumArgumentCaptor.getValue();
        assertThat(testAuditorium.getNumberOfSeats()).isEqualTo(numberOfSeats);

    }

    @Test
    @DisplayName("Update Auditorium throws exceptions")
    void givenIdNumberOfSeats_whenUpdateAuditorium_thenThrowsException() {
        //given
        long id = 1L;
        int numberOfSeats = 10;

        //when
        given(this.repository.findById(id)).willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> this.underTest.updateAuditorium(id, numberOfSeats))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Auditorium with Id " + id + " does not exists");

    }
}