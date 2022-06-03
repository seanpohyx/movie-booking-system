package com.example.booking.seatAuditorium;

import com.example.booking.seat.SeatId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Columns;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeatAuditoriumCK that = (SeatAuditoriumCK) o;
        return auditoriumId == that.auditoriumId && Objects.equals(seatId, that.seatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seatId, auditoriumId);
    }
}
