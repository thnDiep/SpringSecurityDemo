package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.BookingResponse;
import com.example.demo.service.SeatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/seats")
public class SeatController {
    SeatService seatService;

    @PostMapping("/booking/{code}")
    public ApiResponse<BookingResponse> booking(@PathVariable String code){
        return ApiResponse.<BookingResponse>builder()
                .result(seatService.bookSeat(code))
                .build();
    }

    @PostMapping("/payment/{code}/{fakePayment}")
    public ApiResponse<BookingResponse> payment(@PathVariable String code, @PathVariable Boolean fakePayment){
        return ApiResponse.<BookingResponse>builder()
                .result(seatService.payment(code, fakePayment))
                .build();
    }
}
