package com.example.booking.seat;

import com.example.booking.seatAuditorium.SeatAuditorium;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
