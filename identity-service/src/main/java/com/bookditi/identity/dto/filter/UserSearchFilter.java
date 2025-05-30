package com.bookditi.identity.dto.filter;

import java.time.LocalDate;
import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
