package com.example.booking.movie;

import com.example.booking.screening.Screening;
import lombok.*;
import org.w3c.dom.Text;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table
public class Movie {

    @Id
    @SequenceGenerator(
            name = "show_sequence",
            sequenceName = "show_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "show_sequence"
    )
    private Long movieId;

    private String title;

    @Column(
            columnDefinition = "TEXT"
    )
    private String description;

    private Integer duration;

    private String casts;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long createdDateTime;

    private Long updatedDateTime;

    @OneToMany(mappedBy="movie")
    private List<Screening> screeningList;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(movieId, movie.movieId) && Objects.equals(title, movie.title) && Objects.equals(description, movie.description) && Objects.equals(duration, movie.duration) && Objects.equals(casts, movie.casts) && Objects.equals(startDate, movie.startDate) && Objects.equals(endDate, movie.endDate) && Objects.equals(createdDateTime, movie.createdDateTime) && Objects.equals(updatedDateTime, movie.updatedDateTime) && Objects.equals(screeningList, movie.screeningList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, title, description, duration, casts, startDate, endDate, createdDateTime, updatedDateTime, screeningList);
    }
}
