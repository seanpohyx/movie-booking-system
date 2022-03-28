package com.example.booking.screening;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    @Query("SELECT s " +
            "FROM Screening s " +
            "WHERE s.auditorium.auditoriumId = ?1 AND " +
            "s.movie.movieId = ?4 AND " +
            "(s.showTime + ?3) >= ?2 AND " +
            "s.showTime <= ?2 ")
    Optional<Screening> existsByAuditoriumIdAndShowTime(long auditoriumId, long showtime, long duration, long movieId);
}
