package com.example.demo.dto.filter;

import com.example.demo.constant.BookingStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingSearchFilter {
    String username;
    String roomName;
    List<String> status;
    LocalDateTime fromTime;
    LocalDateTime toTime;
}
