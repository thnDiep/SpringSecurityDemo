package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByName(String name);

    Optional<Room> findByName(String name);

    @Query("SELECT r FROM Room r LEFT JOIN FETCH r.seats")
    List<Room> findAllWithSeats();
}
