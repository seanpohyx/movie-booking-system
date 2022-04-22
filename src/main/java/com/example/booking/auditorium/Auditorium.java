package com.example.booking.auditorium;

import com.example.booking.seatAuditorium.SeatAuditorium;
import com.example.booking.screening.Screening;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table
public class Auditorium {

    @Id
    @SequenceGenerator(
            name = "audi_sequence",
            sequenceName = "audi_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "audi_sequence"
    )
    private long auditoriumId;

    private Integer numberOfSeats;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "auditorium")
    private List<Screening> screeningList;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy="auditorium")
    private List<SeatAuditorium> seatAuditoriumList;

}
