package com.example.booking.seatBooking;

import com.example.booking.account.Account;
import com.example.booking.account.AccountRepository;
import com.example.booking.auditorium.Auditorium;
import com.example.booking.exception.BadRequestException;
import com.example.booking.exception.SeatBookingNotFoundException;
import com.example.booking.movie.Movie;
import com.example.booking.screening.Screening;
import com.example.booking.screening.ScreeningRepository;
import com.example.booking.seat.Seat;
import com.example.booking.seat.SeatId;
import com.example.booking.seatAuditorium.SeatAuditorium;
import com.example.booking.seatAuditorium.SeatAuditoriumCK;
import com.example.booking.seatAuditorium.SeatAuditoriumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class})
class SeatBookingServiceTest {

    @Mock
    private SeatAuditoriumRepository seatAuditoriumRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ScreeningRepository screeningRepository;

    @Mock
    private SeatBookingRepository seatBookingRepository;

    @InjectMocks
    private SeatBookingService underTest;

    private SeatBooking seatBooking;
    private SeatId seatId;
    private Seat seat;
    private Auditorium auditorium;
    private SeatAuditorium seatAuditorium;
    private Screening screening;
    private Account account;

    @BeforeEach
    void setUp(){

        this.seatId = SeatId.builder()
                .rowNumber("A")
                .seatNumber(1)
                .build();

        this.seat = Seat.builder()
                .seatId(this.seatId)
                .cost(new BigDecimal(10.10d))
                .build();

        this.auditorium = Auditorium.builder()
                .auditoriumId(1L)
                .numberOfSeats(10)
                .build();

        this.seatAuditorium = SeatAuditorium.builder()
                .id(SeatAuditoriumCK.builder()
                        .auditoriumId(1L)
                        .seatId(this.seatId)
                        .build())
                .seat(this.seat)
                .auditorium(this.auditorium)
                .build();

        this.screening = Screening.builder()
                .screeningId(1L)
                .movie(Movie.builder()
                        .movieId(1L)
                        .build())
                .auditorium(this.auditorium)
                .build();

        this.account = Account.builder()
                .accountId(1L)
                .email("test@gmail.com")
                .build();


        this.seatBooking = SeatBooking.builder()
//                .seatAuditoriumCK(this.seatAuditorium.getId())
                .seatAuditorium(this.seatAuditorium)
                .seatBookingId(1L)
                .screening(this.screening)
                .account(this.account)
                .build();
    }

    @Test
    @DisplayName("get list of seat booking")
    void given_whenGetSeatBookings_thenReturnListOfSeatBooking() {

        //given
        given(this.seatBookingRepository.findAll()).willReturn(List.of(this.seatBooking));
        //when
        List<SeatBooking> testSeatBooking = this.underTest.getSeatBookings();

        //then
        assertThat(testSeatBooking.size()).isEqualTo(1);
        assertThat(testSeatBooking.get(0)).isEqualTo(this.seatBooking);

    }

    @Test
    @DisplayName("get seat booking")
    void givenBookingId_whenGetSeatBooking_thenReturnSeatBooking() {

        //given
        long bookingId = 1L;

        given(this.seatBookingRepository.findById(bookingId)).willReturn(Optional.of(this.seatBooking));

        //when
        SeatBooking testSeatBooking = this.underTest.getSeatBooking(bookingId);

        //then
        assertThat(testSeatBooking).isEqualTo(this.seatBooking);

    }

    @Test
    @DisplayName("get seat booking - throws exceptions for nonexistent seat booking")
    void givenBookingId_whenGetSeatBooking_thenThrowsExceptionForNonexistentSeatBooking() {

        //given
        long bookingId = -1L;

        //when
        given(this.seatBookingRepository.findById(bookingId)).willReturn(Optional.empty());

        //then
        assertThatThrownBy(()->this.underTest.getSeatBooking(bookingId))
                .isInstanceOf(SeatBookingNotFoundException.class)
                .hasMessageContaining("Seat Booking of id " + bookingId + " does not exists");

    }

    @Test
    @DisplayName("get seat booking")
    void givenSeatBookingDto_whenAddSeatBooking_thenReturnSeatBooking() {
        //given
        long bookedTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC.of("+08:00"));
        long auditoriumId = 1L;
        int seatNumber = 1;
        String rowNumber = "A";
        long accountId = 1L;
        long screeningId = 1L;

        SeatBookingDto seatBookingDto = SeatBookingDto.builder()
                .bookedTime(bookedTime)
                .seatNumber(seatNumber)
                .rowNumber(rowNumber)
                .auditoriumId(auditoriumId)
                .screeningId(screeningId)
                .accountId(accountId)
                .build();

        SeatAuditoriumCK seatAuditoriumCK = SeatAuditoriumCK.builder()
                .seatId(SeatId.builder()
                        .rowNumber(seatBookingDto.getRowNumber())
                        .seatNumber(seatBookingDto.getSeatNumber())
                        .build())
                .auditoriumId(seatBookingDto.getAuditoriumId())
                .build();

        given(this.screeningRepository.findById(screeningId)).willReturn(Optional.of(this.screening));
        given(this.accountRepository.findById(accountId)).willReturn(Optional.of(this.account));
        given(this.seatAuditoriumRepository.findById(seatAuditoriumCK)).willReturn(Optional.of(this.seatAuditorium));
        given(this.seatBookingRepository.save(SeatBooking.builder()
                .account(this.account)
                .screening(this.screening)
                .seatAuditorium(this.seatAuditorium)
                .bookedTime(seatBookingDto.getBookedTime())
                .build())
        ).willReturn(this.seatBooking);

        //when
        SeatBooking testSeatBooking = this.underTest.addSeatBooking(seatBookingDto);

        //then
        assertThat(testSeatBooking).isEqualTo(this.seatBooking);

    }

    @Test
    @DisplayName("get seat booking - throw exception for invalid booking time")
    void givenSeatBookingDto_whenAddSeatBooking_thenThrowsExceptionForInvalidBookingTime() {
        //given
        long bookedTime = -1;
        long auditoriumId = 1L;
        int seatNumber = 1;
        String rowNumber = "A";
        long accountId = 1L;
        long screeningId = 1L;

        SeatBookingDto seatBookingDto = SeatBookingDto.builder()
                .bookedTime(bookedTime)
                .seatNumber(seatNumber)
                .rowNumber(rowNumber)
                .auditoriumId(auditoriumId)
                .screeningId(screeningId)
                .accountId(accountId)
                .build();

        //then
        assertThatThrownBy(()->this.underTest.addSeatBooking(seatBookingDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Missing or invalid booking time");
    }

    @Test
    @DisplayName("get seat booking - throw exception for invalid account id")
    void givenSeatBookingDto_whenAddSeatBooking_thenThrowsExceptionForInvalidAccountId() {
        //given
        long bookedTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC.of("+08:00"));
        long auditoriumId = 1L;
        int seatNumber = 1;
        String rowNumber = "A";
        Long accountId = null;
        long screeningId = 1L;

        SeatBookingDto seatBookingDto = SeatBookingDto.builder()
                .bookedTime(bookedTime)
                .seatNumber(seatNumber)
                .rowNumber(rowNumber)
                .auditoriumId(auditoriumId)
                .screeningId(screeningId)
                .accountId(accountId)
                .build();

        //then
        assertThatThrownBy(()->this.underTest.addSeatBooking(seatBookingDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Missing or invalid account id");
    }

    @Test
    @DisplayName("get seat booking - throw exception for invalid screening id")
    void givenSeatBookingDto_whenAddSeatBooking_thenThrowsExceptionForInvalidScreeningId() {
        //given
        long bookedTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC.of("+08:00"));
        long auditoriumId = 1L;
        int seatNumber = 1;
        String rowNumber = "A";
        long accountId = 1L;
        Long screeningId = null;

        SeatBookingDto seatBookingDto = SeatBookingDto.builder()
                .bookedTime(bookedTime)
                .seatNumber(seatNumber)
                .rowNumber(rowNumber)
                .auditoriumId(auditoriumId)
                .screeningId(screeningId)
                .accountId(accountId)
                .build();

        //then
        assertThatThrownBy(()->this.underTest.addSeatBooking(seatBookingDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Missing or invalid screening id");
    }

    @Test
    @DisplayName("get seat booking - throw exception for invalid seat number")
    void givenSeatBookingDto_whenAddSeatBooking_thenThrowsExceptionForInvalidSeatNumber() {
        //given
        long bookedTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC.of("+08:00"));
        long auditoriumId = 1L;
        Integer seatNumber = null;
        String rowNumber = "A";
        long accountId = 1L;
        long screeningId = 1L;

        SeatBookingDto seatBookingDto = SeatBookingDto.builder()
                .bookedTime(bookedTime)
                .seatNumber(seatNumber)
                .rowNumber(rowNumber)
                .auditoriumId(auditoriumId)
                .screeningId(screeningId)
                .accountId(accountId)
                .build();

        //then
        assertThatThrownBy(()->this.underTest.addSeatBooking(seatBookingDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Missing or invalid seat number");
    }

    @Test
    @DisplayName("get seat booking - throw exception for invalid row number")
    void givenSeatBookingDto_whenAddSeatBooking_thenThrowsExceptionForInvalidRowNumber() {
        //given
        long bookedTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC.of("+08:00"));
        long auditoriumId = 1L;
        int seatNumber = 1;
        String rowNumber = "";
        long accountId = 1L;
        long screeningId = 1L;

        SeatBookingDto seatBookingDto = SeatBookingDto.builder()
                .bookedTime(bookedTime)
                .seatNumber(seatNumber)
                .rowNumber(rowNumber)
                .auditoriumId(auditoriumId)
                .screeningId(screeningId)
                .accountId(accountId)
                .build();

        //then
        assertThatThrownBy(()->this.underTest.addSeatBooking(seatBookingDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Missing or invalid row number");
    }

    @Test
    @DisplayName("get seat booking - throw exception for invalid auditorium id")
    void givenSeatBookingDto_whenAddSeatBooking_thenThrowsExceptionForInvalidAuditoriumId() {
        //given
        long bookedTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC.of("+08:00"));
        Long auditoriumId = null;
        int seatNumber = 1;
        String rowNumber = "A";
        long accountId = 1L;
        long screeningId = 1L;

        SeatBookingDto seatBookingDto = SeatBookingDto.builder()
                .bookedTime(bookedTime)
                .seatNumber(seatNumber)
                .rowNumber(rowNumber)
                .auditoriumId(auditoriumId)
                .screeningId(screeningId)
                .accountId(accountId)
                .build();

        //then
        assertThatThrownBy(()->this.underTest.addSeatBooking(seatBookingDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Missing or invalid auditorium id");
    }

    @Test
    @DisplayName("get seat booking - throws exception for non-existing screening")
    void givenSeatBookingDto_whenAddSeatBooking_thenThrowsExceptionsForNonExistingScreening() {
        //given
        long bookedTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC.of("+08:00"));
        long auditoriumId = 1L;
        int seatNumber = 1;
        String rowNumber = "A";
        long accountId = 1L;
        long screeningId = 1L;

        SeatBookingDto seatBookingDto = SeatBookingDto.builder()
                .bookedTime(bookedTime)
                .seatNumber(seatNumber)
                .rowNumber(rowNumber)
                .auditoriumId(auditoriumId)
                .screeningId(screeningId)
                .accountId(accountId)
                .build();

        given(this.screeningRepository.findById(screeningId)).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(()->this.underTest.addSeatBooking(seatBookingDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Screening does not exist with screening Id of " + screeningId);
    }

    @Test
    @DisplayName("get seat booking - throws exception for non-existing account")
    void givenSeatBookingDto_whenAddSeatBooking_thenThrowsExceptionsForNonExistingAccount() {
        //given
        long bookedTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC.of("+08:00"));
        long auditoriumId = 1L;
        int seatNumber = 1;
        String rowNumber = "A";
        long accountId = 1L;
        long screeningId = 1L;

        SeatBookingDto seatBookingDto = SeatBookingDto.builder()
                .bookedTime(bookedTime)
                .seatNumber(seatNumber)
                .rowNumber(rowNumber)
                .auditoriumId(auditoriumId)
                .screeningId(screeningId)
                .accountId(accountId)
                .build();

        given(this.screeningRepository.findById(screeningId)).willReturn(Optional.of(this.screening));
        given(this.accountRepository.findById(accountId)).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> this.underTest.addSeatBooking(seatBookingDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Account does not exist with account Id of " + seatBookingDto.getAccountId());
    }

    @Test
    @DisplayName("get seat booking - throws exception for non-existing SeatAuditorium")
    void givenSeatBookingDto_whenAddSeatBooking_thenThrowsExceptionsForNonExistingSeatAuditorium() {
        //given
        long bookedTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC.of("+08:00"));
        long auditoriumId = 1L;
        int seatNumber = 1;
        String rowNumber = "A";
        long accountId = 1L;
        long screeningId = 1L;

        SeatBookingDto seatBookingDto = SeatBookingDto.builder()
                .bookedTime(bookedTime)
                .seatNumber(seatNumber)
                .rowNumber(rowNumber)
                .auditoriumId(auditoriumId)
                .screeningId(screeningId)
                .accountId(accountId)
                .build();

        SeatAuditoriumCK seatAuditoriumCK = SeatAuditoriumCK.builder()
                .seatId(SeatId.builder()
                        .rowNumber(seatBookingDto.getRowNumber())
                        .seatNumber(seatBookingDto.getSeatNumber())
                        .build())
                .auditoriumId(seatBookingDto.getAuditoriumId())
                .build();

        given(this.screeningRepository.findById(screeningId)).willReturn(Optional.of(this.screening));
        given(this.accountRepository.findById(accountId)).willReturn(Optional.of(this.account));
        given(this.seatAuditoriumRepository.findById(seatAuditoriumCK)).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> this.underTest.addSeatBooking(seatBookingDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(String.format("SeatAuditorium does not exist with row number: %s, seat number: %s and " +
                        "auditoriumId of %s", seatNumber, rowNumber, auditoriumId));
    }

    @Test
    @DisplayName("delete seat booking")
    void givenBookingId_whenDeleteSeatBooking_thenDoNothing() {
        //given
        long bookingId = 1L;

        given(this.seatBookingRepository.existsById(bookingId)).willReturn(true);

        //when
        this.underTest.deleteSeatBooking(bookingId);

        //then
        verify(this.seatBookingRepository, times(1)).deleteById(bookingId);
    }

    @Test
    @DisplayName("delete seat booking - throws exceptions")
    void givenBookingId_whenDeleteSeatBooking_thenThrowsException() {
        //given
        long bookingId = 1L;

        given(this.seatBookingRepository.existsById(bookingId)).willReturn(false);

        //when
        //then
        assertThatThrownBy(()->this.underTest.deleteSeatBooking(bookingId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Seat booking of id" + bookingId + " does not exist");
    }

    @Test
    @DisplayName("update seat booking")
    void givenBookingIdRowNumberSeatNumberAuditoriumIdScreeningIdUserId_whenUpdateSeatBooking_thenDoNothing(){
        //given
        long bookingId = 1L;
        long bookedTime = LocalDateTime.now().minusHours(2).toEpochSecond(ZoneOffset.UTC.of("+08:00"));
        long auditoriumId = 2L;
        int seatNumber = 100;
        String rowNumber = "B";
        long accountId = 2L;
        long screeningId = 2L;

        SeatAuditoriumCK newSeatAuditoriumCK = SeatAuditoriumCK.builder()
                .auditoriumId(auditoriumId)
                .seatId(SeatId.builder()
                        .seatNumber(seatNumber)
                        .rowNumber(rowNumber)
                        .build())
                .build();

        Auditorium newAuditorium = this.auditorium;
        newAuditorium.setAuditoriumId(auditoriumId);

        SeatAuditorium newSeatAuditorium = SeatAuditorium.builder()
                .id(newSeatAuditoriumCK)
                .auditorium(newAuditorium)
                .seat(Seat.builder()
                        .seatId(SeatId.builder()
                                .seatNumber(seatNumber)
                                .rowNumber(rowNumber)
                                .build())
                        .cost(new BigDecimal(10.10d))
                        .build())
                .build();

        Screening newScreening = this.screening;
        newScreening.setScreeningId(screeningId);

        Account newAccount = this.account;
        newAccount.setAccountId(accountId);

        SeatBooking newSeatBooking = this.seatBooking;
        newSeatBooking.setAccount(newAccount);
        newSeatBooking.setScreening(newScreening);
        newSeatBooking.setBookedTime(bookedTime);
        newSeatBooking.setSeatAuditorium(newSeatAuditorium);

        given(this.seatBookingRepository.findById(bookingId)).willReturn(Optional.of(this.seatBooking));
        given(this.seatAuditoriumRepository.findById(newSeatAuditoriumCK)).willReturn(Optional.of(newSeatAuditorium));
        given(this.screeningRepository.findById(screeningId)).willReturn(Optional.of(newScreening));
        given(this.accountRepository.findById(accountId)).willReturn(Optional.of(newAccount));
        given(this.seatBookingRepository.save(newSeatBooking)).willReturn(newSeatBooking);

        //when
        this.underTest.updateSeatBooking(bookingId, rowNumber, seatNumber,
                auditoriumId, screeningId, accountId, bookedTime);
        //then
        ArgumentCaptor<SeatBooking> seatBookingArgumentCaptor =
                ArgumentCaptor.forClass(SeatBooking.class);

        verify(this.seatBookingRepository).save(seatBookingArgumentCaptor.capture());
        SeatBooking capturedSeatBooking = seatBookingArgumentCaptor.getValue();

        assertThat(capturedSeatBooking.getSeatBookingId()).isEqualTo(bookingId);
        assertThat(capturedSeatBooking.getAccount().getAccountId()).isEqualTo(accountId);
        assertThat(capturedSeatBooking.getScreening().getScreeningId()).isEqualTo(screeningId);
        assertThat(capturedSeatBooking.getBookedTime()).isEqualTo(bookedTime);
        assertThat(capturedSeatBooking.getSeatAuditorium().getAuditorium().getAuditoriumId()).isEqualTo(auditoriumId);
        assertThat(capturedSeatBooking.getSeatAuditorium().getSeat().getSeatId().getSeatNumber()).isEqualTo(seatNumber);
        assertThat(capturedSeatBooking.getSeatAuditorium().getSeat().getSeatId().getRowNumber()).isEqualTo(rowNumber);

    }

    @Test
    @DisplayName("update seat booking - throws exceptions for invalid booking id")
    void givenBookingIdRowNumberSeatNumberAuditoriumIdScreeningIdUserId_whenUpdateSeatBooking_thenThrowsExceptionsForInvalidBookingId(){
        //given
        long bookingId = -1L;
        long bookedTime = LocalDateTime.now().minusHours(2).toEpochSecond(ZoneOffset.UTC.of("+08:00"));
        long auditoriumId = 2L;
        int seatNumber = 100;
        String rowNumber = "B";
        long accountId = 2L;
        long screeningId = 2L;

        given(this.seatBookingRepository.findById(bookingId)).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(()->this.underTest.updateSeatBooking(bookingId, rowNumber, seatNumber,
                auditoriumId, screeningId, accountId, bookedTime))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid bookingId :" + bookingId);

    }

    @Test
    @DisplayName("update seat booking - throws exceptions for non-existent SeatAuditorium")
    void givenBookingIdRowNumberSeatNumberAuditoriumIdScreeningIdUserId_whenUpdateSeatBooking_thenThrowsExceptionsForNonExistentSeatAuditorium(){
        //given
        long bookingId = -1L;
        long bookedTime = LocalDateTime.now().minusHours(2).toEpochSecond(ZoneOffset.UTC.of("+08:00"));
        long auditoriumId = 2L;
        int seatNumber = 100;
        String rowNumber = "B";
        long accountId = 2L;
        long screeningId = 2L;

        SeatAuditoriumCK newSeatAuditoriumCK = SeatAuditoriumCK.builder()
                .auditoriumId(auditoriumId)
                .seatId(SeatId.builder()
                        .seatNumber(seatNumber)
                        .rowNumber(rowNumber)
                        .build())
                .build();

        Auditorium newAuditorium = this.auditorium;
        newAuditorium.setAuditoriumId(auditoriumId);

        given(this.seatBookingRepository.findById(bookingId)).willReturn(Optional.of(this.seatBooking));
        given(this.seatAuditoriumRepository.findById(newSeatAuditoriumCK)).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(()->this.underTest.updateSeatBooking(bookingId, rowNumber, seatNumber,
                auditoriumId, screeningId, accountId, bookedTime))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(String.format("SeatAuditorium does not exist with row number: %s, seat number: %s and " +
                        "auditoriumId of %s",rowNumber, seatNumber, auditoriumId));

    }

    @Test
    @DisplayName("update seat booking - throws exceptions for non-existent Screening")
    void givenBookingIdRowNumberSeatNumberAuditoriumIdScreeningIdUserId_whenUpdateSeatBooking_thenThrowsExceptionsForNonExistentSeatScreening(){
        //given
        long bookingId = -1L;
        long bookedTime = LocalDateTime.now().minusHours(2).toEpochSecond(ZoneOffset.UTC.of("+08:00"));
        long auditoriumId = 2L;
        int seatNumber = 100;
        String rowNumber = "B";
        long accountId = 2L;
        long screeningId = 2L;

        SeatAuditoriumCK newSeatAuditoriumCK = SeatAuditoriumCK.builder()
                .auditoriumId(auditoriumId)
                .seatId(SeatId.builder()
                        .seatNumber(seatNumber)
                        .rowNumber(rowNumber)
                        .build())
                .build();

        Auditorium newAuditorium = this.auditorium;
        newAuditorium.setAuditoriumId(auditoriumId);

        SeatAuditorium newSeatAuditorium = SeatAuditorium.builder()
                .id(newSeatAuditoriumCK)
                .auditorium(newAuditorium)
                .seat(Seat.builder()
                        .seatId(SeatId.builder()
                                .seatNumber(seatNumber)
                                .rowNumber(rowNumber)
                                .build())
                        .cost(new BigDecimal(10.10d))
                        .build())
                .build();

        given(this.seatBookingRepository.findById(bookingId)).willReturn(Optional.of(this.seatBooking));
        given(this.seatAuditoriumRepository.findById(newSeatAuditoriumCK)).willReturn(Optional.of(newSeatAuditorium));
        given(this.screeningRepository.findById(screeningId)).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(()->this.underTest.updateSeatBooking(bookingId, rowNumber, seatNumber,
                auditoriumId, screeningId, accountId, bookedTime))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Screening id of " + screeningId + " does not exist");

    }

    @Test
    @DisplayName("update seat booking - throws exceptions for non-existent Account")
    void givenBookingIdRowNumberSeatNumberAuditoriumIdScreeningIdUserId_whenUpdateSeatBooking_thenThrowsExceptionsForNonExistentAccount(){
        //given
        long bookingId = -1L;
        long bookedTime = LocalDateTime.now().minusHours(2).toEpochSecond(ZoneOffset.UTC.of("+08:00"));
        long auditoriumId = 2L;
        int seatNumber = 100;
        String rowNumber = "B";
        long accountId = 2L;
        long screeningId = 2L;

        SeatAuditoriumCK newSeatAuditoriumCK = SeatAuditoriumCK.builder()
                .auditoriumId(auditoriumId)
                .seatId(SeatId.builder()
                        .seatNumber(seatNumber)
                        .rowNumber(rowNumber)
                        .build())
                .build();

        Auditorium newAuditorium = this.auditorium;
        newAuditorium.setAuditoriumId(auditoriumId);

        SeatAuditorium newSeatAuditorium = SeatAuditorium.builder()
                .id(newSeatAuditoriumCK)
                .auditorium(newAuditorium)
                .seat(Seat.builder()
                        .seatId(SeatId.builder()
                                .seatNumber(seatNumber)
                                .rowNumber(rowNumber)
                                .build())
                        .cost(new BigDecimal(10.10d))
                        .build())
                .build();

        Screening newScreening = this.screening;
        newScreening.setScreeningId(screeningId);

        given(this.seatBookingRepository.findById(bookingId)).willReturn(Optional.of(this.seatBooking));
        given(this.seatAuditoriumRepository.findById(newSeatAuditoriumCK)).willReturn(Optional.of(newSeatAuditorium));
        given(this.screeningRepository.findById(screeningId)).willReturn(Optional.of(newScreening));
        given(this.accountRepository.findById(accountId)).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(()->this.underTest.updateSeatBooking(bookingId, rowNumber, seatNumber,
                auditoriumId, screeningId, accountId, bookedTime))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("User id of " + accountId + " does not exist");

    }
}