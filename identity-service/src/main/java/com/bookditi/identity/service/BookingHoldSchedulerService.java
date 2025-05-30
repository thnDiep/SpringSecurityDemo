package com.bookditi.identity.service;

import java.util.Map;
import java.util.concurrent.*;

import jakarta.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import com.bookditi.identity.utility.BookingSystemState;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class BookingHoldSchedulerService {
    ScheduledExecutorService schedulers = Executors.newScheduledThreadPool(10);
    Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    BookingReleaseService bookingReleaseService;
    BookingSystemState bookingSystemState;

    void scheduleBookingRelease(Long bookingId, long timeoutMillis) {
        bookingSystemState.incrementHoldBookingCounter();
        ScheduledFuture<?> task = schedulers.schedule(
                () -> bookingReleaseService.releaseBooking(bookingId), timeoutMillis, TimeUnit.MILLISECONDS);
        scheduledTasks.put(bookingId, task);
    }

    public void cancelSchedule(Long bookingId) {
        ScheduledFuture<?> task = scheduledTasks.remove(bookingId);
        bookingSystemState.decrementHoldBookingCounter();

        if (task != null && !task.isDone()) {
            task.cancel(false);
        }
    }

    @PreDestroy
    public void shutdownScheduler() {
        schedulers.shutdown();
    }
}
