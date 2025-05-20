package com.example.demo.service;

import com.example.demo.constant.BookingStatus;
import com.example.demo.dto.request.BookingRequest;
import com.example.demo.dto.response.BookingResponse;
import com.example.demo.entity.Seat;
import com.example.demo.entity.User;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.BookingMapper;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
public class BookingServiceTest {
    public static boolean invalidFlag = true;
    public static int invalidCounter = 0;

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

    private User user;
    private BookingRequest bookingRequest;
    private BookingResponse bookingResponse;
    private List<Seat> holdSeats;

    @BeforeEach
    void setup() {
        BookingService.isBookingEnable = true;

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
        Mockito.when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        Mockito.when(seatService.holdSeats(anyList())).thenReturn(holdSeats);
        Mockito.when(bookingMapper.toBookingResponse(any())).thenReturn(bookingResponse);

        BookingResponse response = bookingService.bookSeats(bookingRequest);

        Assertions.assertEquals(BookingStatus.WAITING_PAYMENT, response.getStatus());
    }

    @Test
    void testBookSeats_disableSystem_fail() {
        BookingService.isBookingEnable = false;

        var exception = assertThrows(AppException.class, () -> bookingService.bookSeats(bookingRequest));

        Assertions.assertEquals(ErrorCode.BOOKING_SYSTEM_UNDER_MAINTENANCE, exception.getErrorCode());
    }

    @Test
    @WithMockUser(username = "user")
    void testBookSeats_userNotExist_fail() {
        Mockito.when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        var exception = assertThrows(AppException.class, () -> bookingService.bookSeats(bookingRequest));

        Assertions.assertEquals(ErrorCode.USER_NOT_EXISTED, exception.getErrorCode());
    }

    @Test
    void testVolatile() {
        BookingService.isBookingEnable = true;
        BookingServiceTest.invalidFlag = true;

        Thread t1 = new Thread(() -> {
            System.out.println("START VOLATILE - " + BookingService.isBookingEnable);
            while(BookingService.isBookingEnable) {

            }
            System.out.println("END VOLATILE - " + BookingService.isBookingEnable);
            assertFalse(BookingService.isBookingEnable);
        });
        t1.start();

        Thread t2 = new Thread(() -> {
            System.out.println("START INVALID - " + BookingServiceTest.invalidFlag);
            while(BookingServiceTest.invalidFlag) {

            }
            System.out.println("END INVALID = " + BookingServiceTest.invalidFlag);
        });
        t2.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        BookingServiceTest.invalidFlag = false;
        BookingService.isBookingEnable = false;
    }

    @Test
    public void testAtomicVariable() {
        BookingService.currentHoldBooking.set(0);
        BookingServiceTest.invalidCounter = 0;

        Runnable inc = () -> {
            for (int i = 0; i < 1000; i++) {
                BookingService.currentHoldBooking.incrementAndGet();
                BookingServiceTest.invalidCounter++;
            }
        };

        Runnable dec = () -> {
            for (int i = 0; i < 1000; i++) {
                BookingService.currentHoldBooking.decrementAndGet();
                BookingServiceTest.invalidCounter--;
            }
        };

        Thread t1 = new Thread(inc);
        Thread t2 = new Thread(dec);
        Thread t3 = new Thread(inc);
        Thread t4 = new Thread(dec);

        t1.start(); t2.start(); t3.start(); t4.start();

        try {
            t1.join(); t2.join(); t3.join(); t4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(0, BookingService.currentHoldBooking.get());
        System.out.println("Atomic Counter: " + BookingService.currentHoldBooking.get());
        System.out.println("Invalid Counter: " + BookingServiceTest.invalidCounter);
    }
}
