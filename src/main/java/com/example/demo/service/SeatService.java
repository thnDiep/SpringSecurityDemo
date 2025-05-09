package com.example.demo.service;

import com.example.demo.constant.SeatStatus;
import com.example.demo.entity.Room;
import com.example.demo.entity.Seat;
import com.example.demo.repository.SeatRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class SeatService {
    SeatRepository seatRepository;

    public void createSeatList(Room room, int seatNumber) {
        String prefixCode = room.getPrefixCode();

        List<Seat> seats = new ArrayList<>();
        for(int i = 1; i <= seatNumber; i++) {
            Seat seat = new Seat();
            seat.setCode(prefixCode + i);
            seat.setRoom(room);
            seat.setStatus(SeatStatus.AVAILABLE);
            seats.add(seat);
        }
        seatRepository.saveAll(seats);
    }
}
