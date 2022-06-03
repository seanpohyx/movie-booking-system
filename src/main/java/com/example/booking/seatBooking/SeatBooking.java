package com.example.booking.seatBooking;

import com.example.booking.account.Account;
import com.example.booking.screening.Screening;
import com.example.booking.seatAuditorium.SeatAuditorium;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
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
    private Long seatBookingId;

    private Long bookedTime;

    @ManyToOne()
    @JoinColumn(name="accountId", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name="screenId", nullable = false)
    private Screening screening;

    @ManyToOne
    @JoinColumns(value = {
            @JoinColumn(name="seatNumber", referencedColumnName = "seatNumber", nullable = false),
            @JoinColumn(name="auditoriumId", referencedColumnName = "auditoriumId", nullable = false),
            @JoinColumn(name = "rowNumber", referencedColumnName = "rowNumber", nullable = false)
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
