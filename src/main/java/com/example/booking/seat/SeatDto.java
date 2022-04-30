package com.example.booking.seat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeatDto {

    private BigDecimal cost;

    private Integer seatNumber;

    private String rowNumber;

}
