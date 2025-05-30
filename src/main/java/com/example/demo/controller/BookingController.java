package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.filter.BookingSearchFilter;
import com.example.demo.dto.pagination.PaginationResponse;
import com.example.demo.dto.request.BookingRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.BookingResponse;
import com.example.demo.service.BookingService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/bookings")
public class BookingController {
    BookingService bookingService;

    @PostMapping
    public ApiResponse<BookingResponse> createBooking(@RequestBody BookingRequest request) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.bookSeats(request))
                .build();
    }

    @PostMapping("/pay")
    public ApiResponse<BookingResponse> payBooking(@RequestParam Long id, @RequestParam boolean isSuccess) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.payBooking(id, isSuccess))
                .build();
    }

    @PostMapping("/cancel")
    public ApiResponse<BookingResponse> cancelBooking(@RequestParam Long id) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.cancelBooking(id))
                .build();
    }

    @PostMapping("/toggle")
    public ApiResponse<String> toggleBookingSystem(@RequestParam boolean enable) {
        bookingService.toggleBookingSystem(enable);
        return ApiResponse.<String>builder()
                .result("Booking enabled = " + enable)
                .build();
    }

    @GetMapping("/myBooking")
    public ApiResponse<PaginationResponse<BookingResponse>> getMyBooking(
            @RequestBody BookingSearchFilter filter, @RequestParam(defaultValue = "0") int page) {
        return ApiResponse.<PaginationResponse<BookingResponse>>builder()
                .result(bookingService.getMyBooking(filter, page))
                .build();
    }
}
