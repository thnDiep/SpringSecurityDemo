package com.example.demo.service;

import com.example.demo.constant.BookingStatus;
import com.example.demo.constant.SeatStatus;
import com.example.demo.dto.filter.BookingSearchFilter;
import com.example.demo.dto.pagination.PaginationMeta;
import com.example.demo.dto.pagination.PaginationResponse;
import com.example.demo.dto.request.BookingRequest;
import com.example.demo.dto.response.BookingResponse;
import com.example.demo.entity.Booking;
import com.example.demo.entity.Seat;
import com.example.demo.entity.User;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.BookingMapper;
import com.example.demo.mapper.PaginationMapper;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.SeatRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.utility.BookingSystemState;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class BookingService {
    UserRepository userRepository;
    BookingRepository bookingRepository;
    SeatRepository seatRepository;

    SeatService seatService;
    BookingMapper bookingMapper;
    BookingHoldSchedulerService bookingHoldSchedulerService;
    BookingSystemState bookingSystemState;

    @Transactional
    public BookingResponse bookSeats(BookingRequest request) {
        if (!bookingSystemState.isBookingEnable())
            throw new AppException(ErrorCode.BOOKING_SYSTEM_UNDER_MAINTENANCE);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Seat> seats = seatService.holdSeats(request.getSeatIds());

        Booking booking = Booking.builder()
                .user(user)
                .seats(seats)
                .bookingTime(LocalDateTime.now())
                .status(BookingStatus.WAITING_PAYMENT)
                .build();
        bookingRepository.save(booking);

        bookingHoldSchedulerService.scheduleBookingRelease(booking.getId(), 150000);
        return bookingMapper.toBookingResponse(booking);
    }

    @Transactional
    public BookingResponse payBooking(Long id, boolean isSuccess) {
        Booking booking = validateBooking(id);

        if(booking.getStatus() != BookingStatus.WAITING_PAYMENT)
            throw new AppException(ErrorCode.BOOKING_NOT_EXIST);

        if(isSuccess) {
            booking.setStatus(BookingStatus.SUCCESS);
            booking.getSeats().forEach(seat -> seat.setStatus(SeatStatus.BOOKED));
            bookingRepository.save(booking);
            seatRepository.saveAll(booking.getSeats());

            bookingHoldSchedulerService.cancelSchedule(id);
            return bookingMapper.toBookingResponse(booking);
        } else {
            throw new AppException(ErrorCode.FAIL_PAYMENT);
        }
    }

    @Transactional
    public BookingResponse cancelBooking(Long id) {
        Booking booking = validateBooking(id);

        if(booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.EXPIRED)
            throw new AppException(ErrorCode.BOOKING_NOT_EXIST);

        booking.setStatus(BookingStatus.CANCELLED);
        booking.getSeats().forEach(seat -> seat.setStatus(SeatStatus.AVAILABLE));
        bookingRepository.save(booking);
        seatRepository.saveAll(booking.getSeats());

        bookingHoldSchedulerService.cancelSchedule(id);
        return bookingMapper.toBookingResponse(booking);
    }

    private Booking validateBooking(Long id) {
        if(!bookingSystemState.isBookingEnable())
            throw new AppException(ErrorCode.BOOKING_SYSTEM_UNDER_MAINTENANCE);

        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_EXIST));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if(!user.getBookings().contains(booking))
            throw new AppException(ErrorCode.BOOKING_NOT_EXIST);

        return booking;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void toggleBookingSystem(boolean enable) {
        bookingSystemState.setBookingEnable(enable);
    }

    public PaginationResponse<BookingResponse> getMyBooking(BookingSearchFilter filter, int page) {
        filter.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        Pageable pageable = PageRequest.of(page, 2, Sort.by("id"));

        Page<BookingResponse> bookingPage = bookingRepository.searchBooking(filter, pageable).map(bookingMapper::toBookingResponse);
        return PaginationResponse.<BookingResponse>builder()
                .data(bookingPage.getContent())
                .pagination(PaginationMapper.toPaginationMeta(bookingPage))
                .build();
    }

    @Transactional
    public void removeBooking(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_EXIST));
        if(booking.getStatus() == BookingStatus.WAITING_PAYMENT) {
            bookingHoldSchedulerService.cancelSchedule(id);
        }
        booking.getSeats().forEach(seat -> seat.setStatus(SeatStatus.AVAILABLE));
        seatRepository.saveAll(booking.getSeats());
        bookingRepository.delete(booking);
    }
}
