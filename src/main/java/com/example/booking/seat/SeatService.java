package com.example.booking.seat;

import com.example.booking.exception.BadRequestException;
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

    public List<Seat> getSeats() {
        return this.repository.findAll();
    }

    public Seat addNewSeat(SeatDto seatDto) {

        if(seatDto.getSeatNumber() == null || seatDto.getSeatNumber() <= 0)
            throw new BadRequestException("Missing seat number.");

        if(seatDto.getCost() == null)
            throw new BadRequestException("Missing cost.");

        if(seatDto.getRowNumber() == null || seatDto.getRowNumber().isEmpty())
            throw new BadRequestException("Missing row number.");

        boolean isExist = this.repository.existsById(SeatId.builder()
                .rowNumber(seatDto.getRowNumber())
                .seatNumber(seatDto.getSeatNumber())
                .build());

        if(isExist){
            throw new BadRequestException("The seat number " + seatDto.getSeatNumber() +
                    " and row number " + seatDto.getRowNumber() + " already existed");
        }

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
            throw new BadRequestException("the row " + seatId.getRowNumber() + " or seat number " + seatId.getSeatNumber() + " does not exists");

        this.repository.deleteById(seatId);
    }

    public void updateSeat(String rowId, Integer seatNumber, String newRowId, Integer newSeatNumber, BigDecimal newCost) {

        Seat seat = this.repository.findById(SeatId.builder()
                        .seatNumber(seatNumber)
                        .rowNumber(rowId)
                        .build())
                .orElseThrow(()-> new BadRequestException(
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

        this.repository.save(seat);

    }

}
