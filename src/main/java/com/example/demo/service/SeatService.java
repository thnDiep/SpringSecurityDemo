package com.example.demo.service;

import com.example.demo.constant.BookingStatus;
import com.example.demo.constant.SeatStatus;
import com.example.demo.dto.response.BookingResponse;
import com.example.demo.dto.response.SeatResponse;
import com.example.demo.entity.Room;
import com.example.demo.entity.Seat;
import com.example.demo.entity.User;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.SeatMapper;
import com.example.demo.repository.SeatRepository;
import com.example.demo.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class SeatService {
    SeatRepository seatRepository;
    SeatAsyncService seatAsyncService;
    UserRepository userRepository;
    SeatMapper seatMapper;

    @Transactional
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

    public List<BookingResponse> getMyBooking() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        List<Seat> seats = user.getSeats();

        return seats.stream().map(seatMapper::toBookingResponse).toList();
    }

    @Transactional
    public BookingResponse bookSeat(String code) {
        Seat seat = seatRepository.findByCode(code).orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND));
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        log.info("User {} is booking seat {}", user.getUsername(), code);
        if(seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new AppException(ErrorCode.SEAT_ALREADY_BOOKED);
        }

        seat.setStatus(SeatStatus.BLOCKED);
        seat.setUser(user);
        seatRepository.save(seat);

        seatAsyncService.holdSeatAsync(seat.getId());
        return seatMapper.toBookingResponse(seat);
    }

//    @Transactional
//    public BookingResponse payment(String code, Boolean fakePayment){
//        Seat seat = seatRepository.findByCode(code).orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND));
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
//        List<Seat> seats = user.getSeats();
//
//        if(!seats.contains(seat)) {
//            throw new AppException(ErrorCode.SEAT_NOT_FOUND);
//        }
//
//
//    }
}
