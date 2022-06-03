package com.example.booking.config;

import com.example.booking.auditorium.Auditorium;
import com.example.booking.auditorium.AuditoriumRepository;
import com.example.booking.seatAuditorium.SeatAuditorium;
import com.example.booking.seatAuditorium.SeatAuditoriumCK;
import com.example.booking.seatAuditorium.SeatAuditoriumRepository;
import com.example.booking.movie.Movie;
import com.example.booking.movie.MovieRepository;
import com.example.booking.seat.Seat;
import com.example.booking.seat.SeatId;
import com.example.booking.seat.SeatRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class CommandLineConfig {

    @Bean
    CommandLineRunner commandLineRunner(MovieRepository movieRepository,
                                        SeatRepository seatRepository,
                                        AuditoriumRepository auditoriumRepository,
                                        SeatAuditoriumRepository seatAuditoriumRepository) {
        return args -> {

            List<Integer> auditoriumSize = List.of(15, 25, 35, 45, 55);

            List<Auditorium> auditoriumList = auditoriumSize.stream()
                    .map(x -> Auditorium.builder()
                            .numberOfSeats(x)
                            .build())
                    .collect(Collectors.toList());

            auditoriumRepository.saveAll(auditoriumList);

            List<String> rows = List.of("A", "B", "C");
            List<Integer> seatNumbers = List.of(10,20,30,40,50);

            List<Auditorium> auditoriums = auditoriumRepository.findAll();

            for(Auditorium auditorium: auditoriums){
                for(String row: rows){
                    Seat newSeat;
                    for(int number: seatNumbers){
                        newSeat = Seat.builder()
                                .cost(BigDecimal.valueOf(10.90d))
                                .seatId(SeatId.builder()
                                        .rowNumber(row)
                                        .seatNumber(number)
                                        .build())
                                .build();

                        newSeat = seatRepository.save(newSeat);
                        SeatAuditorium seatAuditorium = SeatAuditorium.builder()
                                .id(SeatAuditoriumCK.builder()
                                        .seatId(newSeat.getSeatId())
                                        .auditoriumId(auditorium.getAuditoriumId())
                                        .build())
                                .auditorium(auditorium)
                                .seat(newSeat)
                                .build();

                        seatAuditoriumRepository.save(seatAuditorium);

                    }
                }
            }

            long epochTimeNow = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC.of("+08:00"));
            Movie movie1 = Movie.builder()
                    .createdDateTime(epochTimeNow)
                    .duration(60 + 60 + 52)
                    .startDate(LocalDate.of(2022, Month.MARCH, 02))
                    .endDate(LocalDate.of(2022, Month.MAY, 02))
                    .title("The batman")
                    .casts("Robert Pattison")
                    .updatedDateTime(epochTimeNow)
                    .description("When the Riddler, a sadistic serial killer, begins murdering key political figures in Gotham, Batman is forced to investigate the city's hidden corruption and question his family's involvement.")
                    .build();

            Movie movie2 = Movie.builder()
                    .createdDateTime(epochTimeNow)
                    .duration(60 + 56)
                    .startDate(LocalDate.of(2022, Month.MARCH, 02))
                    .endDate(LocalDate.of(2026, Month.MAY, 02))
                    .title("Uncharted")
                    .casts("Tom Holland")
                    .updatedDateTime(epochTimeNow)
                    .description("Treasure hunter Victor \"Sully\" Sullivan recruits street-smart Nathan Drake to help him recover a 500-year-old lost fortune amassed by explorer Ferdinand Magellan. What starts out as a heist soon becomes a globe-trotting, white-knuckle race to reach the prize before the ruthless Santiago Moncada can get his hands on it. If Sully and Nate can decipher the clues and solve one of the world's oldest mysteries, they stand to find $5 billion in treasure -- but only if they can learn to work together.")
                    .build();

            movieRepository.saveAll(List.of(movie1, movie2));

//            Screening screening1 = Screening.builder()
//                    .showTime(LocalDateTime.of(2022, Month.MARCH, 26,
//                            19, 30, 00))
//                    .movie()
//                    .build();

        };
    }
}
