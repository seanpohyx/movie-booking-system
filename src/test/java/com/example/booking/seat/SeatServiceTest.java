package com.example.booking.seat;

import com.example.booking.exception.BadRequestException;
import com.example.booking.exception.SeatNotFoundException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SeatServiceTest {

    @Mock
    private SeatRepository repository;

    @InjectMocks
    private SeatService underTest;

    @Test
    @DisplayName("Get all seats")
    void given_whenGetSeats_thenReturnListOfSeats() {
        //given
        Seat seat1 = Seat.builder()
                .seatId(SeatId.builder()
                        .rowNumber("A")
                        .seatNumber(1)
                        .build())
                .cost(new BigDecimal(10.10d))
                .build();

        Seat seat2 = Seat.builder()
                .seatId(SeatId.builder()
                        .rowNumber("A")
                        .seatNumber(1)
                        .build())
                .cost(new BigDecimal(10.10d))
                .build();

        given(this.repository.findAll()).willReturn(List.of(seat1, seat2));

        //when
        List<Seat> testSeats = this.underTest.getSeats();

        //then
        assertThat(testSeats.size()).isEqualTo(2);
        assertThat(testSeats.get(0)).isEqualTo(seat1);
        assertThat(testSeats.get(1)).isEqualTo(seat2);
    }

    @Test
    @DisplayName("Get seat by seatId")
    void givenSeatNumberRowId_whenGetSeat_thenReturnSeat() {
        //given
        int seatNumber = 1;
        String rowNumber = "A";

        SeatId seatId = SeatId.builder()
                .rowNumber("A")
                .seatNumber(1)
                .build();

        Seat seat = Seat.builder()
                .seatId(seatId)
                .cost(new BigDecimal(10.10d))
                .build();

        given(this.repository.findById(seatId)).willReturn(Optional.of(seat));

        //when
        Seat testSeat = this.underTest.getSeat(seatNumber, rowNumber);

        //then
        assertThat(testSeat).isEqualTo(seat);
    }

    @Test
    @DisplayName("Get seat by seatId - throws exception not found")
    void givenSeatNumberRowId_whenGetSeat_thenThrowsExceptionNotFound() {
        //given
        int seatNumber = 1;
        String rowNumber = "A";

        SeatId seatId = SeatId.builder()
                .rowNumber("A")
                .seatNumber(1)
                .build();

        Seat seat = Seat.builder()
                .seatId(seatId)
                .cost(new BigDecimal(10.10d))
                .build();

        given(this.repository.findById(seatId)).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> this.underTest.getSeat(seatNumber, rowNumber))
                .isInstanceOf(SeatNotFoundException.class)
                .hasMessageContaining("Seat with seat number " + seatNumber +
                        " and row id " + rowNumber + " not found.");
    }

    @Test
    @DisplayName("Add seat")
    void givenSeat_whenAddNewSeat_thenReturnSeat() {
        //given
        Seat seat = Seat.builder()
                .seatId(SeatId.builder()
                        .rowNumber("A")
                        .seatNumber(1)
                        .build())
                .cost(new BigDecimal(10.10d))
                .build();

        given(this.repository.existsById(
                    SeatId.builder()
                    .rowNumber(seat.getSeatId().getRowNumber())
                    .seatNumber(seat.getSeatId().getSeatNumber())
                    .build()))
                .willReturn(false);

        //when
        this.underTest.addNewSeat(seat);

        //then
        ArgumentCaptor<Seat> seatArgumentCaptor =
                ArgumentCaptor.forClass(Seat.class);

        verify(this.repository).save(seatArgumentCaptor.capture());
        Seat capturedSeat = seatArgumentCaptor.getValue();
        assertThat(capturedSeat.getSeatId().getSeatNumber()).isEqualTo(seat.getSeatId().getSeatNumber());
        assertThat(capturedSeat.getSeatId().getRowNumber()).isEqualTo(seat.getSeatId().getRowNumber());
        assertThat(capturedSeat.getCost()).isEqualTo(seat.getCost());

    }

    @Test
    @DisplayName("Add seat - throw exception for missing seat number")
    void givenSeat_whenAddNewSeat_thenThrowExceptionForSeatNumber() {
        //given
        Seat seat = Seat.builder()
                .seatId(SeatId.builder()
                        .rowNumber("A")
                        .seatNumber(null)
                        .build())
                .cost(new BigDecimal(10.10d))
                .build();

        //when
        //then
        assertThatThrownBy(()-> this.underTest.addNewSeat(seat))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Missing seat number.");

    }

    @Test
    @DisplayName("Add seat - throw exception for missing cost")
    void givenSeatDTO_whenAddNewSeat_thenThrowExceptionForCost() {
        //given
        Seat seat = Seat.builder()
                .seatId(SeatId.builder()
                        .rowNumber("A")
                        .seatNumber(1)
                        .build())
                .cost(null)
                .build();

        //when
        //then
        assertThatThrownBy(()-> this.underTest.addNewSeat(seat))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Missing cost.");

    }

    @Test
    @DisplayName("Add seat - throw exception for missing row number")
    void givenSeat_whenAddNewSeat_thenThrowExceptionForRowNumber() {
        //given
        Seat seat = Seat.builder()
                .seatId(SeatId.builder()
                        .rowNumber(null)
                        .seatNumber(1)
                        .build())
                .cost(new BigDecimal(10.10d))
                .build();

        //when
        //then
        assertThatThrownBy(()-> this.underTest.addNewSeat(seat))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Missing row number.");
    }

    @Test
    @DisplayName("Add seat - throw exception for existing seat id")
    void givenSeatDTO_whenAddNewSeat_thenThrowExceptionForExistingSeatId() {
        //given
        Seat seat = Seat.builder()
                .seatId(SeatId.builder()
                        .rowNumber("A")
                        .seatNumber(1)
                        .build())
                .cost(new BigDecimal(10.10d))
                .build();

        given(this.repository.existsById(
                SeatId.builder()
                        .rowNumber(seat.getSeatId().getRowNumber())
                        .seatNumber(seat.getSeatId().getSeatNumber())
                        .build()))
                .willReturn(true);

        //when
        //then
        assertThatThrownBy(()-> this.underTest.addNewSeat(seat))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("The seat number " + seat.getSeatId().getSeatNumber() +
                        " and row number " + seat.getSeatId().getRowNumber() + " already existed");
    }

    @Test
    @DisplayName("Delete seating")
    void givenRowIdSeatNumber_whenDeleteSeat_thenDoNothing() {
        //given
        String rowId = "A";
        int seatNumber = 10;

        SeatId seatId = SeatId.builder()
                .seatNumber(seatNumber)
                .rowNumber(rowId)
                .build();

        given(this.repository.existsById(seatId)).willReturn(true);
        willDoNothing().given(this.repository).deleteById(seatId);

        //when
        this.underTest.deleteSeat(rowId, seatNumber);

        //then
        verify(this.repository, times(1)).deleteById(seatId);
    }

    @Test
    @DisplayName("Delete seating - throw exception for existing id")
    void givenRowIdSeatNumber_whenDeleteSeat_thenDoThrowException() {
        //given
        String rowId = "A";
        int seatNumber = 10;

        SeatId seatId = SeatId.builder()
                .seatNumber(seatNumber)
                .rowNumber(rowId)
                .build();

        given(this.repository.existsById(seatId)).willReturn(false);

        //when
        //then
        assertThatThrownBy(()-> this.underTest.deleteSeat(rowId, seatNumber))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("the row " + seatId.getRowNumber() + " or seat number " + seatId.getSeatNumber() + " does not exists");

    }

    @Test
    @DisplayName("update seats")
    void givenRowIdSeatNumberNewRowIdNewSeatNumberNewCost_whenUpdateSeat_thenDoNothing() {
        //given
        String rowId = "A";
        int seatNumber = 10;
        BigDecimal newCost = new BigDecimal(10.10d);

        SeatId seatId = SeatId.builder()
                .seatNumber(seatNumber)
                .rowNumber(rowId)
                .build();

        Seat seat = Seat.builder()
                .seatId(seatId)
                .cost(new BigDecimal(1.10d))
                .build();
        given(this.repository.findById(seatId)).willReturn(Optional.of(seat));

        //when
        this.underTest.updateSeat(rowId, seatNumber, Seat.builder()
                        .cost(newCost)
                        .build());

        //then
        ArgumentCaptor<Seat> seatArgumentCaptor =
                ArgumentCaptor.forClass(Seat.class);

        verify(this.repository).save(seatArgumentCaptor.capture());

        Seat capturedSeat = seatArgumentCaptor.getValue();
        assertThat(capturedSeat.getSeatId().getRowNumber()).isEqualTo(rowId);
        assertThat(capturedSeat.getSeatId().getSeatNumber()).isEqualTo(seatNumber);
        assertThat(capturedSeat.getCost()).isEqualTo(newCost);
    }

    @Test
    @DisplayName("update seats - throw exception")
    void givenRowIdSeatNumberNewRowIdNewSeatNumberNewCost_whenUpdateSeat_thenthrowException() {
        //given
        String rowId = "A";
        int seatNumber = 10;
        BigDecimal newCost = new BigDecimal(10.10d);

        SeatId seatId = SeatId.builder()
                .seatNumber(seatNumber)
                .rowNumber(rowId)
                .build();

        given(this.repository.findById(seatId)).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(()-> this.underTest.updateSeat(rowId, seatNumber, Seat.builder()
                        .cost(newCost)
                .build()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("the row " + seatNumber + " or seat number " + rowId + " does not exists");
    }

}