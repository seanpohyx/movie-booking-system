package com.example.booking.seatAuditorium;

import com.example.booking.auditorium.Auditorium;
import com.example.booking.auditorium.AuditoriumRepository;
import com.example.booking.exception.BadRequestException;
import com.example.booking.exception.SeatAuditoriumNotFoundException;
import com.example.booking.seat.Seat;
import com.example.booking.seat.SeatId;
import com.example.booking.seat.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatAuditoriumService {

    private final SeatAuditoriumRepository seatAuditoriumRepository;
    private final SeatRepository seatRepository;
    private final AuditoriumRepository auditoriumRepository;

    @Autowired
    public SeatAuditoriumService(SeatAuditoriumRepository seatAuditoriumRepository, SeatRepository seatRepository, AuditoriumRepository auditoriumRepository) {
        this.seatAuditoriumRepository = seatAuditoriumRepository;
        this.seatRepository = seatRepository;
        this.auditoriumRepository = auditoriumRepository;
    }

    public List<SeatAuditorium> getSeatAuditoriumList(){
        return this.seatAuditoriumRepository.findAll();
    }

    public SeatAuditorium getSeatAuditorium(Integer seatNumber, String rowId, Long auditoriumId) {
        if(seatNumber == null || seatNumber <= 0)
            throw new BadRequestException("Missing or invalid seat number.");

        if(rowId == null || rowId.isEmpty())
            throw new BadRequestException("Missing or invalid row number.");

        if(auditoriumId == null || auditoriumId <= 0)
            throw new BadRequestException("Missing or invalid auditoriumId.");

        SeatAuditoriumCK seatAuditoriumCK = SeatAuditoriumCK.builder()
                .auditoriumId(auditoriumId)
                .seatId(SeatId.builder()
                        .seatNumber(seatNumber)
                        .rowNumber(rowId)
                        .build())
                .build();

        return this.seatAuditoriumRepository.findById(seatAuditoriumCK)
                .orElseThrow(()-> new SeatAuditoriumNotFoundException(
                        String.format("SeatAuditorium does not exist with row number: %s, seat number: %s and " +
                                "auditoriumId of %s", seatNumber, rowId, auditoriumId)));
    }

    public SeatAuditorium addSeatAuditorium(SeatAuditoriumDto seatAuditoriumDto){

        if(seatAuditoriumDto.getSeatNumber() == null || seatAuditoriumDto.getSeatNumber() <= 0)
            throw new BadRequestException("Missing or invalid seat number.");

        if(seatAuditoriumDto.getRowNumber() == null || seatAuditoriumDto.getRowNumber().isEmpty())
            throw new BadRequestException("Missing or invalid row number.");

        if(seatAuditoriumDto.getAuditoriumId() == null || seatAuditoriumDto.getAuditoriumId() <= 0)
            throw new BadRequestException("Missing or invalid auditoriumId.");

        Seat seat = this.seatRepository.findById(SeatId.builder()
                        .rowNumber(seatAuditoriumDto.getRowNumber())
                        .seatNumber(seatAuditoriumDto.getSeatNumber())
                .build())
                .orElseThrow(()->new BadRequestException(
                        String.format("Seat does not exist with row number: %s and seat number: %s", seatAuditoriumDto.getRowNumber(), seatAuditoriumDto.getSeatNumber())));

        Auditorium auditorium = this.auditoriumRepository.findById(seatAuditoriumDto.getAuditoriumId())
                .orElseThrow(()-> new BadRequestException(String.format("auditorium does not exist with auditoriumId: %s", seatAuditoriumDto.getAuditoriumId())));

        return this.seatAuditoriumRepository.save(SeatAuditorium.builder()
                .id(SeatAuditoriumCK.builder()
                        .auditoriumId(auditorium.getAuditoriumId())
                        .seatId(seat.getSeatId())
                        .build())
                .seat(seat)
                .auditorium(auditorium)
                .build());

    }

    public void deleteSeatAuditorium(String rowId, Integer seatNumber, Long auditoriumId) {
        if(seatNumber == null || seatNumber <= 0)
            throw new BadRequestException("Missing or invalid seat number.");

        if(rowId == null || rowId.isEmpty())
            throw new BadRequestException("Missing or invalid row number.");

        if(auditoriumId == null || auditoriumId <= 0)
            throw new BadRequestException("Missing or invalid auditoriumId.");

        SeatAuditoriumCK seatAuditoriumCK = SeatAuditoriumCK.builder()
                .auditoriumId(auditoriumId)
                .seatId(SeatId.builder()
                        .seatNumber(seatNumber)
                        .rowNumber(rowId)
                        .build())
                .build();

        boolean isExists = this.seatAuditoriumRepository.existsById(seatAuditoriumCK);

        if(!isExists)
            throw new BadRequestException(String.format("SeatAuditorium does not exist with row number: %s," +
                    " seat number: %s and auditoriumId: %s", rowId, seatNumber, auditoriumId));

        this.seatAuditoriumRepository.deleteById(seatAuditoriumCK);


    }

}
