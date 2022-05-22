package com.example.booking.seatBooking;

import com.example.booking.account.Account;
import com.example.booking.account.AccountRepository;
import com.example.booking.exception.BadRequestException;
import com.example.booking.exception.SeatBookingNotFoundException;
import com.example.booking.screening.Screening;
import com.example.booking.screening.ScreeningRepository;
import com.example.booking.seat.SeatId;
import com.example.booking.seatAuditorium.SeatAuditorium;
import com.example.booking.seatAuditorium.SeatAuditoriumCK;
import com.example.booking.seatAuditorium.SeatAuditoriumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatBookingService {

    private final SeatBookingRepository seatBookingRepository;
    private final SeatAuditoriumRepository seatAuditoriumRepository;
    private final AccountRepository accountRepository;
    private final ScreeningRepository screeningRepository;

    @Autowired
    public SeatBookingService(SeatBookingRepository seatBookingRepository,
                              SeatAuditoriumRepository seatAuditoriumRepository,
                              AccountRepository accountRepository,
                              ScreeningRepository screeningRepository) {
        this.seatBookingRepository = seatBookingRepository;
        this.seatAuditoriumRepository = seatAuditoriumRepository;
        this.accountRepository = accountRepository;
        this.screeningRepository = screeningRepository;
    }

    public List<SeatBooking> getSeatBookings(){
        return this.seatBookingRepository.findAll();
    }

    public SeatBooking getSeatBooking(Long bookingId){
        return this.seatBookingRepository.findById(bookingId)
                .orElseThrow(()-> new SeatBookingNotFoundException("Seat Booking of id " + bookingId + " does not exists"));
    }

    public SeatBooking addSeatBooking(SeatBookingDto seatBookingDto){
        if(seatBookingDto.getBookedTime() == null || seatBookingDto.getBookedTime() <= 0)
            throw  new BadRequestException("Missing or invalid booking time");

        if(seatBookingDto.getAccountId() == null || seatBookingDto.getAccountId() <= 0)
            throw  new BadRequestException("Missing or invalid account id");

        if(seatBookingDto.getScreeningId() == null || seatBookingDto.getScreeningId() <= 0)
            throw  new BadRequestException("Missing or invalid screening id");

        if(seatBookingDto.getSeatNumber() == null || seatBookingDto.getSeatNumber() <= 0)
            throw  new BadRequestException("Missing or invalid seat number");

        if(seatBookingDto.getRowNumber() == null || seatBookingDto.getRowNumber().isEmpty())
            throw  new BadRequestException("Missing or invalid row number");

        if(seatBookingDto.getAuditoriumId() == null || seatBookingDto.getAuditoriumId() <= 0)
            throw  new BadRequestException("Missing or invalid auditorium id");

        Screening screening = this.screeningRepository.findById(seatBookingDto.getScreeningId())
                .orElseThrow(()->new BadRequestException("Screening does not exist with screening Id of " + seatBookingDto.getScreeningId()));

        Account account = this.accountRepository.findById(seatBookingDto.getAccountId())
                .orElseThrow(()-> new BadRequestException("Account does not exist with account Id of " + seatBookingDto.getAccountId()));

        SeatAuditoriumCK seatAuditoriumCK = SeatAuditoriumCK.builder()
                .seatId(SeatId.builder()
                        .rowNumber(seatBookingDto.getRowNumber())
                        .seatNumber(seatBookingDto.getSeatNumber())
                        .build())
                .auditoriumId(seatBookingDto.getAuditoriumId())
                .build();

        SeatAuditorium seatAuditorium = this.seatAuditoriumRepository.findById(seatAuditoriumCK)
                .orElseThrow(()-> new BadRequestException(
                        String.format("SeatAuditorium does not exist with row number: %s, seat number: %s and " +
                                "auditoriumId of %s", seatBookingDto.getSeatNumber(), seatBookingDto.getRowNumber(), seatBookingDto.getAuditoriumId())));

        return this.seatBookingRepository.save(SeatBooking.builder()
                        .account(account)
                        .screening(screening)
                        .seatAuditorium(seatAuditorium)
                        .bookedTime(seatBookingDto.getBookedTime())
                .build());
    }

    public void deleteSeatBooking(Long bookingId){

        boolean isExist = this.seatBookingRepository.existsById(bookingId);

        if(!isExist)
            throw new BadRequestException("Seat booking of id" + bookingId + " does not exist");

        this.seatBookingRepository.deleteById(bookingId);
    }


    public SeatBooking updateSeatBooking(Long bookingId, SeatBookingDto seatBookingDto) {

        SeatBooking seatBooking = this.seatBookingRepository.findById(bookingId)
                .orElseThrow(()-> new BadRequestException("Invalid bookingId :" + bookingId));

        String rowNumber = seatBookingDto.getRowNumber();
        Integer seatNumber = seatBookingDto.getSeatNumber();
        Long auditoriumId = seatBookingDto.getAuditoriumId();
        Long screeningId = seatBookingDto.getScreeningId();
        Long userId = seatBookingDto.getAccountId();
        Long bookingTime = seatBookingDto.getBookedTime();

        SeatAuditoriumCK currentSeatAuditoriumCK = seatBooking.getSeatAuditorium().getId();

        if(seatNumber != null || seatNumber > 0)
            currentSeatAuditoriumCK.getSeatId().setSeatNumber(seatNumber);

        if(rowNumber != null || !rowNumber.isEmpty())
            currentSeatAuditoriumCK.getSeatId().setRowNumber(rowNumber);

        if(auditoriumId != null || auditoriumId > 0)
            currentSeatAuditoriumCK.setAuditoriumId(auditoriumId);

        SeatAuditorium seatAuditorium = this.seatAuditoriumRepository.findById(currentSeatAuditoriumCK)
                .orElseThrow(()-> new BadRequestException(String.format("SeatAuditorium does not exist with row number: %s, seat number: %s and " +
                        "auditoriumId of %s",rowNumber, seatNumber, auditoriumId)));
        seatBooking.setSeatAuditorium(seatAuditorium);

        if(screeningId != null || screeningId > 0){
            Screening screening = this.screeningRepository.findById(screeningId)
                    .orElseThrow(()-> new BadRequestException("Screening id of " + screeningId + " does not exist"));
            seatBooking.setScreening(screening);
        }

        if(userId != null || userId > 0) {
            Account account = this.accountRepository.findById(userId)
                    .orElseThrow(()-> new BadRequestException("User id of " + userId + " does not exist"));

            seatBooking.setAccount(account);
        }

        if(bookingTime != null || bookingTime > 0){
            seatBooking.setBookedTime(bookingTime);
        }

        return this.seatBookingRepository.save(seatBooking);
    }
}
