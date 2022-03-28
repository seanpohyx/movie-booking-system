package com.example.booking.seat;

import com.example.booking.auditorium.Auditorium;
import com.example.booking.auditorium.AuditoriumRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class SeatConfig {

//    @Bean
//    CommandLineRunner commandLineRunner(SeatRepository seatRepository, AuditoriumRepository auditoriumRepository){
//        return args -> {

//            List<Character> rows = List.of('A', 'B', 'C');
//            List<Integer> seatNumbers = List.of(1,2,3,4,5);
//            List<Seat> seats = new ArrayList<>();
//
//            List<Auditorium> auditoriums = auditoriumRepository.findAll();
//
//            for(Auditorium auditorium: auditoriums){
//                for(char row: rows){
//                    for(int number: seatNumbers){
//                        seats.add(Seat.builder()
//                                .cost(BigDecimal.valueOf(10.90d))
//                                .seatId(SeatId.builder()
//                                        .seatNumber(number)
//                                        .rowNumber(row)
//                                        .build())
//                                .auditorium(auditorium)
//                                .build());
//                    }
//                }
//            }
//
//            seatRepository.saveAll(seats);

//        };
//    }
}
