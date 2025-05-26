package com.example.demo.utility;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Component;

import com.example.demo.entity.Room;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Component
public class RoomCreationQueueManager {
    private final BlockingQueue<Room> queue = new LinkedBlockingQueue<>();

    public void submit(Room room) {
        try {
            queue.put(room);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Thread was interrupted during queue.put()");
        }
    }
}
