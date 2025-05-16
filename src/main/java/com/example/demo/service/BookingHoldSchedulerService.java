package com.example.demo.service;

import com.example.demo.constant.BookingStatus;
import com.example.demo.constant.SeatStatus;
import com.example.demo.entity.Booking;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.SeatRepository;
import jakarta.annotation.PreDestroy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class BookingHoldSchedulerService {
    ScheduledExecutorService schedulers = Executors.newScheduledThreadPool(10);
    Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    BookingReleaseService bookingReleaseService;

    void scheduleBookingRelease(Long bookingId, long timeoutMillis) {
        ScheduledFuture<?> task = schedulers.schedule(() -> bookingReleaseService.releaseBooking(bookingId), timeoutMillis, TimeUnit.MILLISECONDS);
        scheduledTasks.put(bookingId, task);
    }

    public void cancelSchedule(Long bookingId) {
        ScheduledFuture<?> task = scheduledTasks.remove(bookingId);

        if (task != null && !task.isDone()) {
            task.cancel(false);
        }
    }

    @PreDestroy
    public void shutdownScheduler() {
        schedulers.shutdown();
    }
}
