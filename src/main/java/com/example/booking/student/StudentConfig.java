package com.example.booking.student;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Configuration
public class StudentConfig {

//    @Bean
//    CommandLineRunner commandLineRunner(StudentRepository repository){
//        return args -> {
//            Student sherrill = Student.builder()
//                    .dob(LocalDate.of(2000, Month.DECEMBER,2))
//                    .email("sherrill@gmail.com")
//                    .name("sherrill song")
//                    .build();
//
//            Student alex = Student.builder()
//                    .dob(LocalDate.of(2004, Month.MARCH,20))
//                    .email("alex@gmail.com")
//                    .name("alex tan")
//                    .build();
//
//            repository.saveAll(List.of(alex, sherrill));
//
//        };
//    }
}
