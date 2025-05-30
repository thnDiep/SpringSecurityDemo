package com.bookditi.identity.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookditi.identity.constant.BookingStatus;
import com.bookditi.identity.constant.SeatStatus;
import com.bookditi.identity.entity.Booking;
import com.bookditi.identity.repository.BookingRepository;
import com.bookditi.identity.repository.SeatRepository;
import com.bookditi.identity.utility.BookingSystemState;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class BookingReleaseService {
    BookingRepository bookingRepository;
    SeatRepository seatRepository;
    BookingSystemState bookingSystemState;

    @Transactional
    public void releaseBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null || booking.getStatus() != BookingStatus.WAITING_PAYMENT) return;

        bookingSystemState.decrementHoldBookingCounter();
        booking.setStatus(BookingStatus.EXPIRED);
        booking.getSeats().forEach(seat -> seat.setStatus(SeatStatus.AVAILABLE));
        bookingRepository.save(booking);
        seatRepository.saveAll(booking.getSeats());
    }
}
