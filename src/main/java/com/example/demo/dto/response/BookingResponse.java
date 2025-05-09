package com.example.demo.dto.response;

import com.example.demo.constant.BookingStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingResponse {
    String roomName;
    String seatCode;
    BookingStatus status;
}
