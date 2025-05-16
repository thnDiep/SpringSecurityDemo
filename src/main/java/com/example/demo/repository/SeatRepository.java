package com.example.demo.repository;

import com.example.demo.dto.response.SeatStatusStats;
import com.example.demo.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Query(value = "SELECT room_id as roomId, status, COUNT(*) FROM default_schema.seat GROUP BY room_id, status", nativeQuery = true)
    List<SeatStatusStats> seatStats();
}
