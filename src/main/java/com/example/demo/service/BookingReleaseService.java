package com.example.demo.service;

import com.example.demo.constant.BookingStatus;
import com.example.demo.constant.SeatStatus;
import com.example.demo.entity.Booking;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.SeatRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class BookingReleaseService {
    BookingRepository bookingRepository;
    SeatRepository seatRepository;

    @Transactional
    public void releaseBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null || booking.getStatus() != BookingStatus.WAITING_PAYMENT) return;

        booking.setStatus(BookingStatus.EXPIRED);
        booking.getSeats().forEach(seat -> seat.setStatus(SeatStatus.AVAILABLE));
        bookingRepository.save(booking);
        seatRepository.saveAll(booking.getSeats());
    }
}
