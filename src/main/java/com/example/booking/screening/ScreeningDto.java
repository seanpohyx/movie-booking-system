package com.example.booking.screening;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScreeningDto {
    private Long screeningId;
    private Long showTime;
    private Long movieId;
    private Long auditoriumId;

}
