package com.example.booking.movie;

import com.example.booking.screening.Screening;
import lombok.*;
import org.w3c.dom.Text;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
}
