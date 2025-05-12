package com.example.demo.service;

import com.example.demo.config.tenancy.TenantContext;
import com.example.demo.constant.SeatStatus;
import com.example.demo.constant.TenantId;
import com.example.demo.entity.Room;
import com.example.demo.entity.Seat;
import com.example.demo.entity.User;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.SeatRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class SeatServiceTest {

    @Autowired
    private SeatService seatService;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private Room room;

    @BeforeEach
    void setup() {
        TenantContext.setCurrentTenant(TenantId.DEFAULT_SCHEMA);
        room = roomRepository.save(Room.builder().name("test-room").prefixCode("TEST").build());

        Seat seat = Seat.builder()
                .code("TEST11")
                .status(SeatStatus.AVAILABLE)
                .room(room)
                .build();
        seatRepository.save(seat);

        user1 = User.builder().username("test-user1").build();
        user2 = User.builder().username("test-user2").build();

        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    void testConcurrentBooking_shouldThrowOptimisticLock() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1); // dùng để đảm bảo 2 thread khởi động gần nhau

        Callable<Exception> task1 = () -> {
            latch.await();
            setAuth("test-user1");
            try{
                seatService.bookSeat("TEST11");
                return null;
            } catch (Exception e){
                return e;
            }
        };

        Callable<Exception> task2 = () -> {
            latch.await();
            setAuth("test-user2");
            try{
                seatService.bookSeat("TEST11");
                return null;
            } catch (Exception e){
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
        boolean oneFailsWithOptimisticLock =
                (e1 instanceof ObjectOptimisticLockingFailureException && e2 == null) ||
                        (e2 instanceof ObjectOptimisticLockingFailureException && e1 == null);
        assertTrue(oneFailsWithOptimisticLock, "Exactly one call should fail with OptimisticLocking");
    }

    private void setAuth(String username) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, null)
        );
    }

    @Transactional
    @AfterEach
    void cleanUp() {
        roomRepository.delete(room);
        userRepository.delete(user1);
        userRepository.delete(user2);
    }
}
