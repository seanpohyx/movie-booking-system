package com.example.booking.movie;

import com.example.booking.screening.Screening;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDate;
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
