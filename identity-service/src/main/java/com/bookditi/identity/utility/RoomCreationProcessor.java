package com.bookditi.identity.utility;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.bookditi.identity.entity.Room;
import com.bookditi.identity.service.SeatService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class RoomCreationProcessor implements Runnable {
    RoomCreationQueueManager queueManager;
    SeatService seatService;

    @PostConstruct
    public void start() {
        Thread t = new Thread(this, "RoomCreationProcessor");
        t.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Room room = queueManager.getQueue().take();
                seatService.createSeatList(room);
            } catch (InterruptedException e) {
                log.warn("Thread was interrupted, shutting down...");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Failed to create seats: {}", e.getMessage(), e);
            }
        }
    }
}
