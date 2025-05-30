package com.bookditi.identity.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookditi.identity.constant.SeatStatus;
import com.bookditi.identity.dto.response.ApiResponse;
import com.bookditi.identity.dto.response.SeatResponse;
import com.bookditi.identity.service.SeatService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/seats")
public class SeatController {
    SeatService seatService;

    @GetMapping("/stats")
    public ApiResponse<Map<Long, Map<SeatStatus, Long>>> getSeatStats() {
        return ApiResponse.<Map<Long, Map<SeatStatus, Long>>>builder()
                .result(seatService.getSeatStats())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<SeatResponse> getSeatById(@PathVariable Long id) {
        return ApiResponse.<SeatResponse>builder()
                .result(seatService.getSeatById(id))
                .build();
    }
}
