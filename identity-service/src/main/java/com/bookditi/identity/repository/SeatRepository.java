package com.bookditi.identity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bookditi.identity.dto.response.SeatStatusStats;
import com.bookditi.identity.entity.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Query(
            value = "SELECT room_id as roomId, status, COUNT(*) FROM default_schema.seat GROUP BY room_id, status",
            nativeQuery = true)
    List<SeatStatusStats> seatStats();
}
