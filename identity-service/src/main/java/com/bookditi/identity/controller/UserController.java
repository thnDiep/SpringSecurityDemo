package com.bookditi.identity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.web.bind.annotation.*;

import com.bookditi.identity.dto.filter.UserSearchFilter;
import com.bookditi.identity.dto.pagination.PaginationResponse;
import com.bookditi.identity.dto.request.UserCreationRequest;
import com.bookditi.identity.dto.request.UserUpdateRequest;
import com.bookditi.identity.dto.response.ApiResponse;
import com.bookditi.identity.dto.response.UserResponse;
import com.bookditi.identity.exception.AppException;
import com.bookditi.identity.exception.ErrorCode;
import com.bookditi.identity.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "User", description = "User Management")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    JobLauncher jobLauncher;
    Job job;
    UserService userService;

    @PostMapping
    @Operation(summary = "Create a new user", security = @SecurityRequirement(name = ""))
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    public ApiResponse<PaginationResponse<UserResponse>> getUsers(@RequestParam(defaultValue = "0") int page) {
        return ApiResponse.<PaginationResponse<UserResponse>>builder()
                .result(userService.getUsers(page))
                .build();
    }

    @PostMapping("/search")
    public ApiResponse<PaginationResponse<UserResponse>> searchUser(
            @RequestBody UserSearchFilter filter, @RequestParam(defaultValue = "0") int page) {
        return ApiResponse.<PaginationResponse<UserResponse>>builder()
                .result(userService.searchUsers(filter, page))
                .build();
    }

    @GetMapping("/export")
    public ApiResponse<String> exportUsers() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis())
                .toJobParameters();
        try {
            jobLauncher.run(job, jobParameters);
        } catch (JobExecutionAlreadyRunningException
                | JobRestartException
                | JobInstanceAlreadyCompleteException
                | JobParametersInvalidException e) {
            throw new AppException(ErrorCode.FAILED_BATCH);
        }
        return ApiResponse.<String>builder()
                .result("User list has been exported.")
                .build();
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable("userId") Long userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserById(userId))
                .build();
    }

    @PutMapping("{userId}")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable Long userId, @RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    @DeleteMapping("{userId}")
    public ApiResponse<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ApiResponse.<String>builder().result("User has been deleted").build();
    }

    @GetMapping("/myProfile")
    public ApiResponse<UserResponse> getMyProfile() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyProfile())
                .build();
    }
}
