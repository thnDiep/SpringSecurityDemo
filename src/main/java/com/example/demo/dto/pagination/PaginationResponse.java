package com.example.demo.dto.pagination;

import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginationResponse<T> {
    List<T> data;
    PaginationMeta pagination;
}
