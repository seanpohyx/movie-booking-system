package com.example.booking.movie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieDto {

    private Long movieId;

    private String title;

    private String description;

    private Integer duration;

    private String casts;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long createdDate;

    private Long updatedTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieDto movieDto = (MovieDto) o;
        return Objects.equals(movieId, movieDto.movieId) && Objects.equals(title, movieDto.title) && Objects.equals(description, movieDto.description) && Objects.equals(duration, movieDto.duration) && Objects.equals(casts, movieDto.casts) && Objects.equals(startDate, movieDto.startDate) && Objects.equals(endDate, movieDto.endDate) && Objects.equals(createdDate, movieDto.createdDate) && Objects.equals(updatedTime, movieDto.updatedTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, title, description, duration, casts, startDate, endDate, createdDate, updatedTime);
    }
}
