package com.example.demo.utility;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "JWT_SIGNER_KEY=testkey123"
})
public class BookingSystemStateTest {
    @InjectMocks
    BookingSystemState bookingSystemState;

    public static boolean invalidFlag = true;
    public static int invalidCounter = 0;

    @Test
    void testVolatile() {
        bookingSystemState.setBookingEnable(true);
        BookingSystemStateTest.invalidFlag = true;

        Thread t1 = new Thread(() -> {
            System.out.println("START VOLATILE - " + bookingSystemState.isBookingEnable());
            while (bookingSystemState.isBookingEnable()) {

            }
            System.out.println("END VOLATILE - " + bookingSystemState.isBookingEnable());
            assertFalse(bookingSystemState.isBookingEnable());
        });
        t1.start();

        Thread t2 = new Thread(() -> {
            System.out.println("START INVALID - " + BookingSystemStateTest.invalidFlag);
            while (BookingSystemStateTest.invalidFlag) {

            }
            System.out.println("END INVALID = " + BookingSystemStateTest.invalidFlag);
        });
        t2.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        BookingSystemStateTest.invalidFlag = false;
        bookingSystemState.setBookingEnable(false);
    }

    @Test
    public void testAtomicVariable() {
        bookingSystemState.setHoldBookingCounter(0);
        BookingSystemStateTest.invalidCounter = 0;

        Runnable inc = () -> {
            for (int i = 0; i < 1000; i++) {
                bookingSystemState.incrementHoldBookingCounter();
                BookingSystemStateTest.invalidCounter++;
            }
        };

        Runnable dec = () -> {
            for (int i = 0; i < 1000; i++) {
                bookingSystemState.decrementHoldBookingCounter();
                BookingSystemStateTest.invalidCounter--;
            }
        };

        Thread t1 = new Thread(inc);
        Thread t2 = new Thread(dec);
        Thread t3 = new Thread(inc);
        Thread t4 = new Thread(dec);

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(0, bookingSystemState.getHoldBookingCounter());
        System.out.println("Atomic Counter: " + bookingSystemState.getHoldBookingCounter());
        System.out.println("Invalid Counter: " + BookingSystemStateTest.invalidCounter);
    }
}
