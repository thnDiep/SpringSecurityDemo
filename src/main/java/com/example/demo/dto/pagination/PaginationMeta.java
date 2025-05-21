package com.example.demo.dto.pagination;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginationMeta {
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;
    private int numberOfElements;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;
}
