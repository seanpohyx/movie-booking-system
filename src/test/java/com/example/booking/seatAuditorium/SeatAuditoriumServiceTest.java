package com.example.booking.seatAuditorium;

import com.example.booking.auditorium.Auditorium;
import com.example.booking.auditorium.AuditoriumRepository;
import com.example.booking.exception.BadRequestException;
import com.example.booking.exception.SeatAuditoriumNotFoundException;
import com.example.booking.seat.Seat;
import com.example.booking.seat.SeatId;
import com.example.booking.seat.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SeatAuditoriumServiceTest {

    @Mock
    private SeatAuditoriumRepository seatAuditoriumRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private AuditoriumRepository auditoriumRepository;

    @InjectMocks
    private SeatAuditoriumService underTest;

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
    @DisplayName("Get all seatAuditoriums")
    void given_whenGetSeatAuditoriumList_thenReturnListOfSeatAuditorium(){
        //given
        given(this.seatAuditoriumRepository.findAll()).willReturn(List.of(this.seatAuditorium));

        //when
        List<SeatAuditorium> testSeatAuditoriumList = this.underTest.getSeatAuditoriumList();

        //then
        assertThat(testSeatAuditoriumList.size()).isEqualTo(1);
        assertThat(testSeatAuditoriumList.get(0)).isEqualTo(this.seatAuditorium);
    }

    @Test
    @DisplayName("Get seatAuditorium by id")
    void givenSeatNumberRowIdAuditoriumId_whenGetSeatAuditorium_thenReturnSeatAuditorium(){
        //given
        String rowId = "A";
        Integer seatNumber = 1;
        Long auditoriumId = 1L;

        SeatAuditoriumCK seatAuditoriumCK = SeatAuditoriumCK.builder()
                .auditoriumId(auditoriumId)
                .seatId(SeatId.builder()
                        .seatNumber(seatNumber)
                        .rowNumber(rowId)
                        .build())
                .build();

        given(this.seatAuditoriumRepository.findById(seatAuditoriumCK)).willReturn(Optional.of(this.seatAuditorium));

        //when
        SeatAuditorium testSeatAuditorium = this.underTest.getSeatAuditorium(seatNumber, rowId, auditoriumId);

        //then
        assertThat(testSeatAuditorium).isEqualTo(this.seatAuditorium);

    }

    @Test
    @DisplayName("Get seatAuditorium by id - throws exceptions for invalid seat number")
    void givenSeatNumberRowIdAuditoriumId_whenGetSeatAuditorium_thenThrowsExceptionsForInvalidSeatNumber(){
        //given
        String rowId = "A";
        Integer seatNumber = null;
        Long auditoriumId = 1L;

        //when
        //then
        assertThatThrownBy(()->this.underTest.getSeatAuditorium(seatNumber, rowId, auditoriumId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Missing or invalid seat number.");

    }

    @Test
    @DisplayName("Get seatAuditorium by id - throws exceptions for invalid row Id")
    void givenSeatNumberRowIdAuditoriumId_whenGetSeatAuditorium_thenThrowsExceptionsForInvalidRowId(){
        //given
        String rowId = "";
        Integer seatNumber = 1;
        Long auditoriumId = 1L;

        //when
        //then
        assertThatThrownBy(()->this.underTest.getSeatAuditorium(seatNumber, rowId, auditoriumId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Missing or invalid row number.");

    }

    @Test
    @DisplayName("Get seatAuditorium by id - throws exceptions for invalid auditorium id")
    void givenSeatNumberRowIdAuditoriumId_whenGetSeatAuditorium_thenThrowsExceptionsForInvalidAuditoriumId(){
        //given
        String rowId = "A";
        Integer seatNumber = 1;
        Long auditoriumId = null;

        //when
        //then
        assertThatThrownBy(()->this.underTest.getSeatAuditorium(seatNumber, rowId, auditoriumId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Missing or invalid auditoriumId.");
    }

    @Test
    @DisplayName("Get seatAuditorium by id - throws exceptions for non-existent seatAuditorium")
    void givenSeatNumberRowIdAuditoriumId_whenGetSeatAuditorium_thenThrowsExceptionsForNonexistentSeatAuditorium(){
        //given
        String rowId = "A";
        Integer seatNumber = 1;
        Long auditoriumId = 1L;

        SeatAuditoriumCK seatAuditoriumCK = SeatAuditoriumCK.builder()
                .auditoriumId(auditoriumId)
                .seatId(SeatId.builder()
                        .seatNumber(seatNumber)
                        .rowNumber(rowId)
                        .build())
                .build();

        given(this.seatAuditoriumRepository.findById(seatAuditoriumCK)).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(()->this.underTest.getSeatAuditorium(seatNumber, rowId, auditoriumId))
                .isInstanceOf(SeatAuditoriumNotFoundException.class)
                .hasMessageContaining(String.format("SeatAuditorium does not exist with row number: %s, seat number: %s and " +
                        "auditoriumId of %s", seatNumber, rowId, auditoriumId));

    }

    @Test
    @DisplayName("Add seatAuditorium")
    void givenSeatAuditoriumDto_whenAddSeatAuditorium_thenReturnSeatAuditorium(){
        //given
        SeatAuditoriumDto seatAuditoriumDto = SeatAuditoriumDto.builder()
                .rowNumber("A")
                .seatNumber(1)
                .auditoriumId(1L)
                .build();

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

        given(this.seatRepository.findById(seatId)).willReturn(Optional.of(seat));
        given(this.auditoriumRepository.findById(1L)).willReturn(Optional.of(auditorium));

        //when
        this.underTest.addSeatAuditorium(seatAuditoriumDto);

        //then
        ArgumentCaptor<SeatAuditorium> seatAuditoriumArgumentCaptor =
                ArgumentCaptor.forClass(SeatAuditorium.class);

        verify(this.seatAuditoriumRepository).save(seatAuditoriumArgumentCaptor.capture());
        SeatAuditorium capturedSeatAuditorium = seatAuditoriumArgumentCaptor.getValue();
        assertThat(capturedSeatAuditorium.getSeat()).isEqualTo(seat);
        assertThat(capturedSeatAuditorium.getAuditorium()).isEqualTo(auditorium);

    }

    @Test
    @DisplayName("Add seatAuditorium - throw exception for invalid seat number")
    void givenSeatAuditoriumDto_whenAddSeatAuditorium_thenThrowsExceptionForInvalidSeatNumber(){
        //given
        SeatAuditoriumDto seatAuditoriumDto = SeatAuditoriumDto.builder()
                .rowNumber("A")
                .auditoriumId(1L)
                .build();

        //when
        //then
        assertThatThrownBy(() -> this.underTest.addSeatAuditorium(seatAuditoriumDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Missing or invalid seat number.");
    }

    @Test
    @DisplayName("Add seatAuditorium - throw exception for invalid row number")
    void givenSeatAuditoriumDto_whenAddSeatAuditorium_thenThrowsExceptionForInvalidRowNumber(){
        //given
        SeatAuditoriumDto seatAuditoriumDto = SeatAuditoriumDto.builder()
                .seatNumber(1)
                .build();

        //when
        //then
        assertThatThrownBy(() -> this.underTest.addSeatAuditorium(seatAuditoriumDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Missing or invalid row number.");
    }

    @Test
    @DisplayName("Add seatAuditorium - throw exception for invalid auditorium Id")
    void givenSeatAuditoriumDto_whenAddSeatAuditorium_thenThrowsExceptionForInvalidAuditoriumId(){
        //given
        SeatAuditoriumDto seatAuditoriumDto = SeatAuditoriumDto.builder()
                .seatNumber(1)
                .rowNumber("A")
                .build();

        //when
        //then
        assertThatThrownBy(() -> this.underTest.addSeatAuditorium(seatAuditoriumDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Missing or invalid auditoriumId.");
    }

    @Test
    @DisplayName("Add seatAuditorium - throws exceptions for not existing seat")
    void givenSeatAuditoriumDto_whenAddSeatAuditorium_thenThrowsExceptionsForNonexistentSeat(){
        //given
        SeatAuditoriumDto seatAuditoriumDto = SeatAuditoriumDto.builder()
                .rowNumber("A")
                .seatNumber(1)
                .auditoriumId(1L)
                .build();

        SeatId seatId = SeatId.builder()
                .rowNumber("A")
                .seatNumber(1)
                .build();

        given(this.seatRepository.findById(seatId)).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> this.underTest.addSeatAuditorium(seatAuditoriumDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(String.format("Seat does not exist with row number: %s and seat number: %s", seatAuditoriumDto.getRowNumber(), seatAuditoriumDto.getSeatNumber()));
    }

    @Test
    @DisplayName("Add seatAuditorium - throws exception for non-existing auditorium")
    void givenSeatAuditoriumDto_whenAddSeatAuditorium_thenThrowsExceptionsForNonexistingAuditorium(){
        //given
        SeatAuditoriumDto seatAuditoriumDto = SeatAuditoriumDto.builder()
                .rowNumber("A")
                .seatNumber(1)
                .auditoriumId(1L)
                .build();

        SeatId seatId = SeatId.builder()
                .rowNumber("A")
                .seatNumber(1)
                .build();

        Seat seat = Seat.builder()
                .seatId(seatId)
                .cost(new BigDecimal(10.10d))
                .build();

        given(this.seatRepository.findById(seatId)).willReturn(Optional.of(seat));
        given(this.auditoriumRepository.findById(1L)).willReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> this.underTest.addSeatAuditorium(seatAuditoriumDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(String.format("auditorium does not exist with auditoriumId: %s", seatAuditoriumDto.getAuditoriumId()));
    }

    @Test
    @DisplayName("Delete seatAuditorium")
    void givenRowIdSeatNumberAuditoriumId_whenDeleteSeatAuditorium_thenDoNothing(){
        //given
        String rowId = "A";
        Integer seatNumber = 1;
        Long auditoriumId = 1L;

        SeatAuditoriumCK seatAuditoriumCK = SeatAuditoriumCK.builder()
                .auditoriumId(auditoriumId)
                .seatId(SeatId.builder()
                        .seatNumber(seatNumber)
                        .rowNumber(rowId)
                        .build())
                .build();

        given(this.seatAuditoriumRepository.existsById(seatAuditoriumCK)).willReturn(true);

        //when
        this.underTest.deleteSeatAuditorium(rowId, seatNumber, auditoriumId);

        //then
        verify(this.seatAuditoriumRepository, times(1)).deleteById(seatAuditoriumCK);
    }

    @Test
    @DisplayName("Delete seatAuditorium - throws exceptions for invalid seat number")
    void givenRowIdSeatNumberAuditoriumId_whenDeleteSeatAuditorium_thenThrowsExceptionsForInvalidSeatNumber(){
        //given
        String rowId = "A";
        Integer seatNumber = 0;
        Long auditoriumId = 1L;

        //when
        //then
        assertThatThrownBy(() -> this.underTest.deleteSeatAuditorium(rowId, seatNumber, auditoriumId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Missing or invalid seat number.");
    }

    @Test
    @DisplayName("Delete seatAuditorium - throws exceptions for invalid row number")
    void givenRowIdSeatNumberAuditoriumId_whenDeleteSeatAuditorium_thenThrowsExceptionsForInvalidRowNumber(){
        //given
        String rowId = "";
        Integer seatNumber = 1;
        Long auditoriumId = 1L;

        //when
        //then
        assertThatThrownBy(() -> this.underTest.deleteSeatAuditorium(rowId, seatNumber, auditoriumId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Missing or invalid row number.");
    }

    @Test
    @DisplayName("Delete seatAuditorium - throws exceptions for invalid auditoriumId")
    void givenRowIdSeatNumberAuditoriumId_whenDeleteSeatAuditorium_thenThrowsExceptionsForInvalidAuditoriumId(){
        //given
        String rowId = "A";
        Integer seatNumber = 1;
        Long auditoriumId = null;

        //when
        //then
        assertThatThrownBy(() -> this.underTest.deleteSeatAuditorium(rowId, seatNumber, auditoriumId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Missing or invalid auditoriumId.");
    }

    @Test
    @DisplayName("Delete seatAuditorium - throws exceptions for invalid non-existent seatAuditorium")
    void givenRowIdSeatNumberAuditoriumId_whenDeleteSeatAuditorium_thenThrowsExceptionForNonexistingAuditorium(){
        //given
        String rowId = "A";
        Integer seatNumber = 1;
        Long auditoriumId = 1L;

        SeatAuditoriumCK seatAuditoriumCK = SeatAuditoriumCK.builder()
                .auditoriumId(auditoriumId)
                .seatId(SeatId.builder()
                        .seatNumber(seatNumber)
                        .rowNumber(rowId)
                        .build())
                .build();

        given(this.seatAuditoriumRepository.existsById(seatAuditoriumCK)).willReturn(false);

        //when
        //then
        assertThatThrownBy(() -> this.underTest.deleteSeatAuditorium(rowId, seatNumber, auditoriumId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(String.format("SeatAuditorium does not exist with row number: %s," +
                        " seat number: %s and auditoriumId: %s", rowId, seatNumber, auditoriumId));
    }


}