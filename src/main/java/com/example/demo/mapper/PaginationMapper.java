package com.example.demo.mapper;

import com.example.demo.dto.pagination.PaginationMeta;
import org.springframework.data.domain.Page;

public class PaginationMapper {
    public static PaginationMeta toPaginationMeta(Page<?> page) {
        return PaginationMeta.builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .numberOfElements(page.getNumberOfElements())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
