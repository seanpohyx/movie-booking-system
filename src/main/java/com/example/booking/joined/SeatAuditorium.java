package com.example.booking.joined;

import com.example.booking.auditorium.Auditorium;
import com.example.booking.seat.Seat;
import com.example.booking.seatBoooking.SeatBooking;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class SeatAuditorium {

    @EmbeddedId
    SeatAuditoriumCK id;

    @ManyToOne
    @MapsId("auditoriumId")
    @JoinColumn(name = "auditoriumId", referencedColumnName = "auditoriumId")
    private Auditorium auditorium;

    @ManyToOne
    @MapsId("seatId")
    @JoinColumns({
            @JoinColumn(name="rowNumber"),
            @JoinColumn(name="seatNumber")
    })
    private Seat seat;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "seatAuditorium")
    private List<SeatBooking> seatBookingList;

}
