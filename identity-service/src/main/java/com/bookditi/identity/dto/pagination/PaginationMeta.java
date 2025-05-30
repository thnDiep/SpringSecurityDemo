package com.bookditi.identity.dto.pagination;

import lombok.*;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationMeta implements Serializable {
    private static final long serialVersionUID = 1L;

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
