package com.example.demo.controller;

import com.example.demo.constant.SeatStatus;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.SeatResponse;
import com.example.demo.dto.response.SeatStatusStats;
import com.example.demo.service.SeatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/seats")
public class SeatController {
    SeatService seatService;

    @GetMapping("/stats")
    public ApiResponse<Map<Long, Map<SeatStatus, Long>>> getSeatStats(){
        return ApiResponse.<Map<Long, Map<SeatStatus, Long>>> builder()
                .result(seatService.getSeatStats())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<SeatResponse> getSeatById(@PathVariable Long id){
        return ApiResponse.<SeatResponse>builder()
                .result(seatService.getSeatById(id))
                .build();
    }
}
