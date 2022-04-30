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
    private long screeningId;
    private long showTime;
    private long movieId;
    private long auditoriumId;



}
