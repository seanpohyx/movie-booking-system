package com.example.booking.seatBooking;

import com.example.booking.seatAuditorium.SeatAuditorium;
import com.example.booking.screening.Screening;
import com.example.booking.account.Account;
import com.example.booking.seatAuditorium.SeatAuditoriumCK;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
@Builder
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeatBooking that = (SeatBooking) o;
        return seatBookingId == that.seatBookingId && bookedTime == that.bookedTime && Objects.equals(account, that.account) && Objects.equals(screening, that.screening) && Objects.equals(seatAuditorium, that.seatAuditorium);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seatBookingId, bookedTime, account, screening, seatAuditorium);
    }
}
