package com.example.demo.mapper;

import org.springframework.data.domain.Page;

import com.example.demo.dto.pagination.PaginationMeta;

public class PaginationMapper {
    private PaginationMapper() {}

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
