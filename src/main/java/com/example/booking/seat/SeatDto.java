package com.example.booking.seat;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeatDto {

    private BigDecimal cost;

    private Integer seatNumber;

    private String rowNumber;

}
