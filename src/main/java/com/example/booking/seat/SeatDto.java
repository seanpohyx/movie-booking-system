package com.example.booking.seat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeatDto {

    private BigDecimal cost;

    private Integer seatNumber;

    private String rowNumber;

}
