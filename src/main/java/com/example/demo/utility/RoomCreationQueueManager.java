package com.example.demo.utility;

import com.example.demo.entity.Room;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Getter
@Component
public class RoomCreationQueueManager {
    private final BlockingQueue<Room> queue = new LinkedBlockingQueue<>();

    public void submit(Room room) {
        try {
            queue.put(room);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
