package com.example.booking.seat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class SeatService {

    private final SeatRepository repository;

    @Autowired
    public SeatService(SeatRepository repository) {
        this.repository = repository;
    }

    public List<Seat> getSeat() {
        return this.repository.findAll();
    }

    public Seat addNewSeat(SeatDto seatDto) {

        if(seatDto.getSeatNumber() == null || seatDto.getSeatNumber() <= 0)
            throw new IllegalStateException("Missing seat number.");

        if(seatDto.getCost() == null)
            throw new IllegalStateException("Missing cost.");

        if(seatDto.getRowNumber() == null || seatDto.getRowNumber().isEmpty())
            throw new IllegalStateException("Missing row number.");

        return this.repository.save(Seat.builder()
                .seatId(SeatId.builder()
                        .rowNumber(seatDto.getRowNumber())
                        .seatNumber(seatDto.getSeatNumber())
                        .build())
                .cost(seatDto.getCost())
                .build());
    }

    public void deleteSeat(String rowId, Integer seatNumber) {

        SeatId seatId = SeatId.builder()
                .seatNumber(seatNumber)
                .rowNumber(rowId)
                .build();

        boolean isExists = this.repository.existsById(seatId);

        if(!isExists)
            throw new IllegalStateException("the row " + seatId.getRowNumber() + " or seat number " + seatId.getSeatNumber() + " does not exists");

        this.repository.deleteById(seatId);
    }

    public Seat updateSeat(String rowId, Integer seatNumber, String newRowId, Integer newSeatNumber, BigDecimal newCost) {

        Seat seat = this.repository.findById(SeatId.builder()
                        .seatNumber(seatNumber)
                        .rowNumber(rowId)
                        .build())
                .orElseThrow(()-> new IllegalStateException(
                        "the row " + seatNumber + " or seat number " + rowId + " does not exists"));

        if(newRowId != null &&
                !newRowId.isEmpty() &&
                !Objects.equals(newRowId, rowId)){
            seat.getSeatId().setRowNumber(newRowId);
        }

        if(newSeatNumber != null &&
                newSeatNumber > 0 &&
                newSeatNumber != seatNumber){
            seat.getSeatId().setSeatNumber(newSeatNumber);
        }

        if(newCost != null &&
                newCost != seat.getCost()){
            seat.setCost(newCost);
        }

        return this.repository.save(seat);

    }
}
