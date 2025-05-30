package com.bookditi.identity.service;

import java.io.IOException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.bookditi.identity.config.websocket.AdminDashboardHandler;
import com.bookditi.identity.utility.BookingSystemState;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingMonitorService {
    BookingSystemState bookingSystemState;
    AdminDashboardHandler webSocketHandler;

    @NonFinal
    private boolean lastEnable = true;

    @NonFinal
    private int lastHold = 0;

    @Scheduled(fixedRate = 1000)
    public void sendStateToDashboard() throws IOException {
        boolean currentEnable = bookingSystemState.isBookingEnable();
        int currentHold = bookingSystemState.getHoldBookingCounter();

        if (currentEnable != lastEnable || currentHold != lastHold) {
            lastEnable = currentEnable;
            lastHold = currentHold;

            String payload = String.format("{\"bookingEnable\":%s, \"currentHold\":%d}", currentEnable, currentHold);
            TextMessage message = new TextMessage(payload);

            for (WebSocketSession session : webSocketHandler.getSessions()) {
                if (session.isOpen()) {
                    session.sendMessage(message);
                }
            }
        }
    }
}
