package com.bookditi.identity.dto.response;

import com.bookditi.identity.constant.SeatStatus;
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
