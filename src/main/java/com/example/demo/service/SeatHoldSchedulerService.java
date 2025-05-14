package com.example.demo.service;

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
public class SeatHoldSchedulerService {
    SeatService seatService;
    ScheduledExecutorService schedulers = Executors.newScheduledThreadPool(10);
    Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public void holdSeat(String code, long timeoutMillis) {
        ScheduledFuture<?> task =  schedulers.schedule(() -> seatService.releaseSeat(code), timeoutMillis, TimeUnit.MILLISECONDS);
        scheduledTasks.put(code, task);
//        throw new RuntimeException("Test Error"); // Test Transaction rollback
    }

    public void cancelHold(String code, boolean isSuccessPayment) {
        ScheduledFuture<?> task = scheduledTasks.remove(code);
        if(task != null && !task.isDone()) {
            task.cancel(false);
        }

        if(!isSuccessPayment)
            seatService.releaseSeat(code);
    }
}
