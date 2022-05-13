package com.example.booking.seatBooking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatBookingDto {

    private Long seatBookingId;

    private Long bookedTime;

    private Long auditoriumId;

    private Integer seatNumber;

    private String rowNumber;

    private Long accountId;

    private Long screeningId;

}
