package com.example.booking.seat;

import com.example.booking.exception.BadRequestException;
import com.example.booking.exception.SeatNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Seat getSeat(int seatNumber, String rowId) {
        return this.repository.findById(SeatId.builder()
                        .seatNumber(seatNumber)
                        .rowNumber(rowId)
                .build())
                .orElseThrow(()-> new SeatNotFoundException("Seat with seat number " + seatNumber +
                        " and row id " + rowId + " not found."));
    }

    public Seat addNewSeat(Seat seat) {

        if(seat.getSeatId().getSeatNumber() == null || seat.getSeatId().getSeatNumber() <= 0)
            throw new BadRequestException("Missing seat number.");

        if(seat.getCost() == null)
            throw new BadRequestException("Missing cost.");

        if(seat.getSeatId().getRowNumber() == null || seat.getSeatId().getRowNumber().isEmpty())
            throw new BadRequestException("Missing row number.");

        boolean isExist = this.repository.existsById(SeatId.builder()
                .rowNumber(seat.getSeatId().getRowNumber())
                .seatNumber(seat.getSeatId().getSeatNumber())
                .build());

        if(isExist){
            throw new BadRequestException("The seat number " + seat.getSeatId().getSeatNumber() +
                    " and row number " + seat.getSeatId().getRowNumber() + " already existed");
        }

        return this.repository.save(Seat.builder()
                .seatId(SeatId.builder()
                        .rowNumber(seat.getSeatId().getRowNumber())
                        .seatNumber(seat.getSeatId().getSeatNumber())
                        .build())
                .cost(seat.getCost())
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

    public Seat updateSeat(String rowId, Integer seatNumber, Seat newSeat){
        Seat seat = this.repository.findById(SeatId.builder()
                        .seatNumber(seatNumber)
                        .rowNumber(rowId)
                        .build())
                .orElseThrow(()-> new BadRequestException(
                        "the row " + seatNumber + " or seat number " + rowId + " does not exists"));

        if(newSeat.getCost() != null &&
                newSeat.getCost() != seat.getCost()){
            seat.setCost(newSeat.getCost());
        }

        return this.repository.save(seat);

    }

}
