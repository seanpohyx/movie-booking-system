package com.example.booking.seatAuditorium;

import com.example.booking.seat.SeatId;
import lombok.*;
import org.hibernate.annotations.Columns;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class SeatAuditoriumCK implements Serializable {


    @Columns(columns = {
            @Column(name = "row_number"),
            @Column(name = "seat_number")
    })
    private SeatId seatId;

    @Column(name = "auditorium_id")
    private long auditoriumId;
}
