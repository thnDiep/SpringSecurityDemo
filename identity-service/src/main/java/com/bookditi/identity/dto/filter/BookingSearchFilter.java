package com.bookditi.identity.dto.filter;

import java.time.LocalDateTime;
import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
