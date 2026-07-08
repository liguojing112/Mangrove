package com.mangrove.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResult<T> {

    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int size;

    public static <T> PageResult<T> of(org.springframework.data.domain.Page<T> page) {
        return PageResult.<T>builder()
                .content(page.getContent())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .size(page.getSize())
                .build();
    }
}
