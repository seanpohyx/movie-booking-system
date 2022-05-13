package com.example.booking.seatAuditorium;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatAuditoriumDto {

    private Long auditoriumId;

    private Integer seatNumber;

    private String rowNumber;

}
