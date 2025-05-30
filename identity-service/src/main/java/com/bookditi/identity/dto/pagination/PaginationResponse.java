package com.bookditi.identity.dto.pagination;

import java.io.Serializable;
import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginationResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    List<T> data;
    PaginationMeta pagination;
}
