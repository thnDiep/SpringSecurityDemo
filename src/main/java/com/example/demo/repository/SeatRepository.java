package com.example.demo.repository;

import com.example.demo.dto.response.SeatStatusStats;
import com.example.demo.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    Optional<Seat> findByCode(String code);

    @Query(value = "SELECT status, COUNT(*) FROM seat GROUP BY status", nativeQuery = true)
    List<SeatStatusStats> seatStats();
}
