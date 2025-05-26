package com.example.demo.utility;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingSystemState {
    @Setter
    @Getter
    volatile boolean bookingEnable = true;

    AtomicInteger currentHoldBooking = new AtomicInteger(0);

    public void setHoldBookingCounter(int value) {
        currentHoldBooking.set(value);
    }

    public void incrementHoldBookingCounter() {
        currentHoldBooking.incrementAndGet();
    }

    public void decrementHoldBookingCounter() {
        currentHoldBooking.decrementAndGet();
    }

    public int getHoldBookingCounter() {
        return currentHoldBooking.get();
    }
}
