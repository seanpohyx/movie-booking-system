package com.example.booking.seat;
import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class SeatId implements Serializable{

    private Integer seatNumber;
    private String rowNumber;

}
