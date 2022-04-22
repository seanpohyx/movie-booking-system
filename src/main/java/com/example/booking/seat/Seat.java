package com.example.booking.seat;

import com.example.booking.seatAuditorium.SeatAuditorium;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table
public class Seat {

    private BigDecimal cost;

    @EmbeddedId
    private SeatId seatId;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy="seat")
    private List<SeatAuditorium> seatAuditoriumList;


}
