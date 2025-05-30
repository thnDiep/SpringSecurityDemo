package com.bookditi.identity.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.bookditi.identity.constant.BookingStatus;
import com.bookditi.identity.dto.request.BookingRequest;
import com.bookditi.identity.dto.response.BookingResponse;
import com.bookditi.identity.entity.Seat;
import com.bookditi.identity.entity.User;
import com.bookditi.identity.exception.AppException;
import com.bookditi.identity.exception.ErrorCode;
import com.bookditi.identity.mapper.BookingMapper;
import com.bookditi.identity.repository.BookingRepository;
import com.bookditi.identity.repository.UserRepository;
import com.bookditi.identity.utility.BookingSystemState;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {"JWT_SIGNER_KEY=testkey123"})
public class BookingServiceTest {
    @InjectMocks
    private BookingService bookingService;

    @Mock
    private SeatService seatService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingHoldSchedulerService bookingHoldSchedulerService;

    @Mock
    private BookingSystemState bookingSystemState;

    private User user;
    private BookingRequest bookingRequest;
    private BookingResponse bookingResponse;
    private List<Seat> holdSeats;

    @BeforeEach
    void setup() {
        user = User.builder().id(100L).username("user").build();

        bookingRequest = BookingRequest.builder().seatIds(List.of(50L)).build();

        holdSeats = new ArrayList<>();
        holdSeats.add(Seat.builder().id(50L).build());

        bookingResponse = BookingResponse.builder()
                .status(BookingStatus.WAITING_PAYMENT)
                .bookingTime(LocalDateTime.now())
                .build();
    }

    @Test
    @WithMockUser(username = "user")
    void testBookSeats_success() {
        // Arrange
        Mockito.when(bookingSystemState.isBookingEnable()).thenReturn(true);
        Mockito.when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        Mockito.when(seatService.holdSeats(anyList())).thenReturn(holdSeats);
        Mockito.when(bookingMapper.toBookingResponse(any())).thenReturn(bookingResponse);

        BookingResponse response = bookingService.bookSeats(bookingRequest);

        Assertions.assertEquals(BookingStatus.WAITING_PAYMENT, response.getStatus());
    }

    @Test
    void testBookSeats_disableSystem_fail() {
        Mockito.when(bookingSystemState.isBookingEnable()).thenReturn(false);

        var exception = assertThrows(AppException.class, () -> bookingService.bookSeats(bookingRequest));

        Assertions.assertEquals(ErrorCode.BOOKING_SYSTEM_UNDER_MAINTENANCE, exception.getErrorCode());
    }

    @Test
    @WithMockUser(username = "user")
    void testBookSeats_userNotExist_fail() {
        Mockito.when(bookingSystemState.isBookingEnable()).thenReturn(true);
        Mockito.when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        var exception = assertThrows(AppException.class, () -> bookingService.bookSeats(bookingRequest));

        Assertions.assertEquals(ErrorCode.USER_NOT_EXISTED, exception.getErrorCode());
    }
}
