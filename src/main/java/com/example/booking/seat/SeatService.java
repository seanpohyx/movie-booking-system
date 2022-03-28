package com.example.booking.seat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

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
}
