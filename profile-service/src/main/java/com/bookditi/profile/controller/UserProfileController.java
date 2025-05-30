package com.bookditi.profile.controller;

import com.bookditi.profile.dto.request.UserProfileRequest;
import com.bookditi.profile.dto.response.ApiResponse;
import com.bookditi.profile.dto.response.UserProfileResponse;
import com.bookditi.profile.service.UserProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("users")
public class UserProfileController {
    UserProfileService userProfileService;

    @PostMapping
    ApiResponse<UserProfileResponse> createUserProfile(@RequestBody UserProfileRequest request) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.createUserProfile(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<UserProfileResponse>> getUserProfiles() {
        return ApiResponse.<List<UserProfileResponse>>builder()
                .result(userProfileService.getUserProfiles())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<UserProfileResponse> createUserProfile(@PathVariable String id) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.getUserProfile(id))
                .build();
    }

    @PostMapping("/{id}")
    ApiResponse<UserProfileResponse> updateUserProfile(@RequestBody UserProfileRequest request, @PathVariable String id) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.updateUserProfile(request, id))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> updateUserProfile(@PathVariable String id) {
        userProfileService.deleteUserProfile(id);
        return ApiResponse.<String>builder()
                .result("User profile has been deleted.")
                .build();
    }
}
