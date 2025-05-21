package com.example.demo.controller;

import com.example.demo.dto.UserSearchFilter;
import com.example.demo.dto.pagination.PaginationResponse;
import com.example.demo.dto.request.UserCreationRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.BookingResponse;
import com.example.demo.dto.response.SeatResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.service.BookingService;
import com.example.demo.service.SeatService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.List;


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
    public ApiResponse<PaginationResponse<UserResponse>> searchUser(@RequestBody UserSearchFilter filter, @RequestParam(defaultValue = "0") int page)  {
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
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
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
    public ApiResponse<UserResponse> updateUser(@PathVariable Long userId, @RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    @DeleteMapping("{userId}")
    public ApiResponse<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ApiResponse.<String>builder()
                .result("User has been deleted")
                .build();
    }

    @GetMapping("/myProfile")
    public ApiResponse<UserResponse> getMyProfile() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyProfile())
                .build();
    }


}
