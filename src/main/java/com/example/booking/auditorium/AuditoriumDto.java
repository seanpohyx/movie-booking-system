package com.example.booking.auditorium;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AuditoriumDto {

    private long auditoriumId;
    private Integer numberOfSeats;

}
