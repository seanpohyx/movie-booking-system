package com.example.booking.auditorium;

import com.example.booking.seatAuditorium.SeatAuditorium;
import com.example.booking.screening.Screening;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Auditorium that = (Auditorium) o;
        return auditoriumId == that.auditoriumId && Objects.equals(numberOfSeats, that.numberOfSeats) && Objects.equals(screeningList, that.screeningList) && Objects.equals(seatAuditoriumList, that.seatAuditoriumList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(auditoriumId, numberOfSeats, screeningList, seatAuditoriumList);
    }
}
