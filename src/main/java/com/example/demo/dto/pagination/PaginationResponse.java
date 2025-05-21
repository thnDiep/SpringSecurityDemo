package com.example.demo.dto.pagination;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginationResponse <T> {
    List<T> data;
    PaginationMeta pagination;
}
