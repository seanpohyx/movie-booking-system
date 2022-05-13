package com.example.booking.movie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT m FROM Movie m " +
            "WHERE m.startDate <= ?1" +
            "AND m.endDate >= ?1")
    List<Movie> findNowShowing(LocalDate date);
}
