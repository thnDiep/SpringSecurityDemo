package com.example.demo.service;

import com.example.demo.constant.SeatStatus;
import com.example.demo.entity.Seat;
import com.example.demo.repository.SeatRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class SeatAsyncService {
    SeatRepository seatRepository;

    @Async
    public void holdSeatAsync(Long seatId) {
        try {
            // Đợi 10 phút (600_000 ms)
            Thread.sleep(5000);

            // Kiểm tra lại trạng thái ghế sau 10 phút
            Optional<Seat> optionalSeat = seatRepository.findById(seatId);
            if (optionalSeat.isPresent()) {
                Seat seat = optionalSeat.get();

                // Nếu vẫn BLOCKED và chưa thanh toán (chưa CONFIRMED)
                if (seat.getStatus() == SeatStatus.BLOCKED) {
                    seat.setStatus(SeatStatus.AVAILABLE);
                    seat.setUser(null);
                    seatRepository.save(seat);
                    log.info("⏳ Ghế {} đã được giải phóng do không thanh toán.", seat.getCode());
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Giữ chỗ ghế {} bị gián đoạn", seatId);
        }
    }
}
