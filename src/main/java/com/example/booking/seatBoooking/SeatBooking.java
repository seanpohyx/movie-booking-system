package com.example.booking.seatBoooking;

import com.example.booking.joined.SeatAuditorium;
import com.example.booking.screening.Screening;
import com.example.booking.seat.SeatId;
import com.example.booking.account.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class SeatBooking {

    @Id
    @SequenceGenerator(
            name="booking_sequence",
            sequenceName = "booking_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "booking_sequence"
    )
    private long seatBookingId;

    private long bookedTime;

    private SeatId seatId;

    @ManyToOne()
    @JoinColumn(name="accountId", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name="screenId", nullable = false)
    private Screening screening;

    @ManyToOne
    @JoinColumns(value = {
            @JoinColumn(name="seatNumber", referencedColumnName = "seatNumber", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name="auditoriumId", referencedColumnName = "auditoriumId", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "rowNumber", referencedColumnName = "rowNumber", nullable = false, insertable = false, updatable = false)
    })
    private SeatAuditorium seatAuditorium;

}
