package com.example.booking.movie;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class MovieDto {

    private long movieId;

    private String title;

    private String description;

    private Integer duration;

    private String casts;

    private LocalDate startDate;

    private LocalDate endDate;

    private long createdDate;

    private long updatedTime;

}
