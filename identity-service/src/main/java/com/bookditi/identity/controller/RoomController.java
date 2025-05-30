package com.bookditi.identity.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.bookditi.identity.dto.request.RoomRequest;
import com.bookditi.identity.dto.response.ApiResponse;
import com.bookditi.identity.dto.response.RoomResponse;
import com.bookditi.identity.service.RoomService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/rooms")
public class RoomController {
    RoomService roomService;

    @GetMapping
    public ApiResponse<List<RoomResponse>> getRooms() {
        return ApiResponse.<List<RoomResponse>>builder()
                .result(roomService.getRooms())
                .build();
    }

    @GetMapping("{id}")
    public ApiResponse<RoomResponse> getRoomById(@PathVariable Long id) {
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.getRoomById(id))
                .build();
    }

    @PostMapping
    public ApiResponse<String> createRoom(@RequestBody RoomRequest request) {
        roomService.createRoom(request);
        return ApiResponse.<String>builder()
                .result("Room " + request.getName() + " has been created.")
                .build();
    }

    @DeleteMapping("{id}")
    public ApiResponse<String> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ApiResponse.<String>builder()
                .result("Room " + id + " has been created.")
                .build();
    }
}
