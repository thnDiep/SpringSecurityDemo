package com.example.demo.dto.request;

import java.time.LocalDate;
import java.util.Set;

import jakarta.validation.constraints.Size;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    @Size(min = 8, message = "INVALID_PASSWORD")
    String password;

    String firstName;
    String lastName;
    LocalDate dob;
    Set<String> roles;
}
