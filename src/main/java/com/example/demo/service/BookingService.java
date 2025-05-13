package com.example.demo.service;

import com.example.demo.constant.BookingStatus;
import com.example.demo.constant.SeatStatus;
import com.example.demo.dto.response.BookingResponse;
import com.example.demo.dto.response.SeatStatusStats;
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

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class BookingService {
    SeatRepository seatRepository;
    UserRepository userRepository;
    SeatMapper seatMapper;
    SeatHoldSchedulerService seatHoldSchedulerService;

    public List<SeatStatusStats> getBookingStats() {
        return seatRepository.seatStats();
    }

    @Transactional
    public BookingResponse bookingSeat(String code) {
        Seat seat = seatRepository.findByCode(code).orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND));
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if(seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new AppException(ErrorCode.SEAT_ALREADY_BOOKED);
        }

        seat.setStatus(SeatStatus.BLOCKED);
        seat.setUser(user);
        seatRepository.save(seat);

        seatHoldSchedulerService.holdSeat(code, 100000);
        return seatMapper.toBookingResponse(seat);
    }

    @Transactional
    public BookingResponse payment(String code, Boolean fakePayment){
        Seat seat = seatRepository.findByCode(code).orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND));
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        List<Seat> seats = user.getSeats();

        if(seat.getStatus() != SeatStatus.BLOCKED || !seats.contains(seat)) {
            throw new AppException(ErrorCode.SEAT_NOT_FOUND);
        }

        BookingResponse booking = seatMapper.toBookingResponse(seat);
        if(fakePayment) {
            seat.setStatus(SeatStatus.BOOKED);
            seatHoldSchedulerService.cancelHold(code, true);
            booking.setStatus(BookingStatus.SUCCESS);
        } else {
            booking.setStatus(BookingStatus.FAIL_PAYMENT);
        }
        return booking;
    }
}
