package com.example.demo.dto.response;

import com.example.demo.constant.BookingStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingResponse {
    Long id;
    RoomBookingResponse room;
    List<SeatBookingResponse> seats;
    BookingStatus status;
    LocalDateTime bookingTime;
}
