package com.example.booking.screening;

import com.example.booking.auditorium.Auditorium;
import com.example.booking.movie.Movie;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table
public class Screening {

    @Id
    @SequenceGenerator(
            name="movie_sequence",
            sequenceName = "movie_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "movie_sequence"
    )
    private long screeningId;

    private long showTime;

    @ManyToOne
    @JoinColumn(name="movieId", nullable = false)
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "auditoriumId", nullable = false)
    private Auditorium auditorium;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Screening screening = (Screening) o;
        return screeningId == screening.screeningId && showTime == screening.showTime && Objects.equals(movie, screening.movie) && Objects.equals(auditorium, screening.auditorium);
    }

    @Override
    public int hashCode() {
        return Objects.hash(screeningId, showTime, movie, auditorium);
    }
}
