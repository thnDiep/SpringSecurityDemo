package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1003, "User not existed", HttpStatus.NOT_FOUND),
    INVALID_USERNAME(1004, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1005, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "Unauthorized", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at leas {min}", HttpStatus.BAD_REQUEST),
    INVALID_TENANT_ID(1009, "Tenant ID is required for authentication", HttpStatus.UNAUTHORIZED),
    FAILED_BATCH(1010, "Error batch processing", HttpStatus.INTERNAL_SERVER_ERROR),
    SQL_ERROR(1011, "Failed to execute SQL script statement", HttpStatus.INTERNAL_SERVER_ERROR),
    ROOM_NAME_EXISTED(1012, "This room name existed", HttpStatus.CONFLICT),
    ROOM_CODE_PREFIX_EXISTED(1013, "This code prefix existed", HttpStatus.CONFLICT),
    SEAT_NOT_FOUND(1014, "The seat not existed", HttpStatus.NOT_FOUND),
    SEAT_ALREADY_BOOKED(1015, "The seat has already booked", HttpStatus.BAD_REQUEST),
    SEAT_HOLD_EXCEPTION(1016, "Seat hold interrupted", HttpStatus.INTERNAL_SERVER_ERROR),
    ROOM_NOT_EXIST(1017, "The room not existed", HttpStatus.NOT_FOUND),
    BOOKING_SYSTEM_UNDER_MAINTENANCE(1018, "System is under maintenance", HttpStatus.INTERNAL_SERVER_ERROR),
    BOOKING_NOT_EXIST(1019, "The booking not existed", HttpStatus.BAD_REQUEST),
    FAIL_PAYMENT(1019, "The payment is failed", HttpStatus.BAD_REQUEST),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
