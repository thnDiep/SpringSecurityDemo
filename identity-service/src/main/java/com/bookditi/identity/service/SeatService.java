package com.bookditi.identity.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookditi.identity.constant.SeatStatus;
import com.bookditi.identity.dto.response.SeatResponse;
import com.bookditi.identity.dto.response.SeatStatusStats;
import com.bookditi.identity.entity.Room;
import com.bookditi.identity.entity.Seat;
import com.bookditi.identity.exception.AppException;
import com.bookditi.identity.exception.ErrorCode;
import com.bookditi.identity.mapper.SeatMapper;
import com.bookditi.identity.repository.SeatRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class SeatService {
    SeatRepository seatRepository;
    SeatMapper seatMapper;

    @Transactional
    public void createSeatList(Room room) {
        List<Seat> seats = new ArrayList<>();

        for (int row = 0; row < room.getTotalRows(); row++) {
            for (int col = 1; col <= room.getTotalCols(); col++) {
                String code = String.valueOf((char) ('A' + row)) + col;

                Seat seat = new Seat();
                seat.setCode(code);
                seat.setStatus(SeatStatus.AVAILABLE);
                seat.setRoom(room);
                seats.add(seat);
            }
        }
        seatRepository.saveAll(seats);
    }

    @Transactional
    public List<Seat> holdSeats(List<Long> seatIds) {
        List<Seat> seats = seatRepository.findAllById(seatIds);

        if (seats.size() != seatIds.size()) throw new AppException(ErrorCode.SEAT_NOT_FOUND);

        for (Seat seat : seats) {
            if (seat.getStatus() != SeatStatus.AVAILABLE) throw new AppException(ErrorCode.SEAT_ALREADY_BOOKED);

            seat.setStatus(SeatStatus.HOLD);
        }

        seatRepository.saveAll(seats);
        return seats;
    }

    public SeatResponse getSeatById(Long id) {
        return seatMapper.toSeatResponse(
                seatRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Map<Long, Map<SeatStatus, Long>> getSeatStats() {
        return seatRepository.seatStats().stream()
                .collect(Collectors.groupingBy(
                        SeatStatusStats::getRoomId,
                        Collectors.groupingBy(
                                seat -> SeatStatus.valueOf(seat.getStatus()),
                                Collectors.summingLong(SeatStatusStats::getCount))));
    }
}
