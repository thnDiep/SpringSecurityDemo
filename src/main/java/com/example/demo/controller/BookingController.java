package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.BookingResponse;
import com.example.demo.dto.response.SeatStatusStats;
import com.example.demo.service.BookingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/booking")
public class BookingController {
    BookingService bookingService;

    @GetMapping("/stats")
    public ApiResponse<List<SeatStatusStats>> getBookingStats(){
        return ApiResponse.<List<SeatStatusStats>>builder()
                .result(bookingService.getBookingStats())
                .build();
    }

    @PostMapping("/{code}")
    public ApiResponse<BookingResponse> booking(@PathVariable String code){
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.bookingSeat(code))
                .build();
    }

    @PostMapping("/{code}/payment/{fakePayment}")
    public ApiResponse<BookingResponse> payment(@PathVariable String code, @PathVariable Boolean fakePayment){
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.payment(code, fakePayment))
                .build();
    }
}
