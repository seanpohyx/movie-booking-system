package com.example.booking.seat;
import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class SeatId implements Serializable{

    private Integer seatNumber;
    private String rowNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeatId seatId = (SeatId) o;
        return Objects.equals(seatNumber, seatId.seatNumber) && Objects.equals(rowNumber, seatId.rowNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seatNumber, rowNumber);
    }
}
