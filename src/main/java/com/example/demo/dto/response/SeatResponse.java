package com.example.demo.dto.response;

import com.example.demo.constant.SeatStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SeatResponse {
    Long id;
    Long roomId;
    String code;
    SeatStatus status;
}
