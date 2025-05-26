package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.example.demo.config.tenancy.TenantContext;
import com.example.demo.constant.TenantId;
import com.example.demo.dto.request.BookingRequest;
import com.example.demo.dto.response.BookingResponse;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.SeatRepository;
import com.example.demo.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {"JWT_SIGNER_KEY=testkey123"})
public class BookingConcurrencyServiceTest {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private RoomRepository roomRepository;

    private BookingRequest bookingRequest;

    private BookingResponse bookingResponse;

    @BeforeEach
    void setup() {
        TenantContext.setCurrentTenant(TenantId.DEFAULT_SCHEMA);
        bookingRequest = BookingRequest.builder().seatIds(List.of(50L)).build();
    }

    @Test
    void testConcurrentBooking_shouldThrowOptimisticLock() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1); // dùng để đảm bảo 2 thread khởi động gần nhau

        Callable<Exception> task1 = () -> {
            latch.await();
            setAuth("default_user");
            try {
                bookingResponse = bookingService.bookSeats(bookingRequest);
                return null;
            } catch (Exception e) {
                System.out.println("User 1: " + e);
                return e;
            }
        };

        Callable<Exception> task2 = () -> {
            latch.await();
            setAuth("default_user1");
            try {
                bookingResponse = bookingService.bookSeats(bookingRequest);
                return null;
            } catch (Exception e) {
                System.out.println("User 2: " + e);
                return e;
            }
        };

        Future<Exception> f1 = executor.submit(task1);
        Future<Exception> f2 = executor.submit(task2);
        latch.countDown(); // mở khoá cho cả 2 chạy gần đồng thời
        Exception e1 = f1.get();
        Exception e2 = f2.get();
        executor.shutdown();

        // kiểm tra có đúng 1 thread bị lỗi OptimisticLock
        boolean oneFailsWithOptimisticLock = (e1 instanceof ObjectOptimisticLockingFailureException && e2 == null)
                || (e2 instanceof ObjectOptimisticLockingFailureException && e1 == null);
        assertTrue(oneFailsWithOptimisticLock, "Exactly one call should fail with OptimisticLocking");
    }

    private void setAuth(String username) {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(username, null, null));
    }

    @AfterEach
    void cleanup() {
        bookingService.removeBooking(bookingResponse.getId());
    }
}
