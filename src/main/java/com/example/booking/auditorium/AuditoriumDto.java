package com.example.booking.auditorium;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditoriumDto {

    private long auditoriumId;
    private Integer numberOfSeats;

}
