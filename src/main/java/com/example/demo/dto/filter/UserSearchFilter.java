package com.example.demo.dto.filter;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSearchFilter {
    String keyword;
    LocalDate fromDob;
    LocalDate toDob;
    List<String> roles;
}
