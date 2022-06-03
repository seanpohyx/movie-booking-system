package com.example.booking.seat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
