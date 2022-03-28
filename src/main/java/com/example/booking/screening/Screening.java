package com.example.booking.screening;

import com.example.booking.auditorium.Auditorium;
import com.example.booking.movie.Movie;
import lombok.*;

import javax.persistence.*;

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
}
