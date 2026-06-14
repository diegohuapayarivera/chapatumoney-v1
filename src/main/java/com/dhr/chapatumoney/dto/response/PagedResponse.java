package com.dhr.chapatumoney.dto.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@Builder
public class PagedResponse<T> {
    private List<T> data;
    private PageMeta meta;

    public static <T> PagedResponse<T> from(Page<T> page) {
        return PagedResponse.<T>builder()
                .data(page.getContent())
                .meta(PageMeta.builder()
                        .page(page.getNumber())
                        .size(page.getSize())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .build())
                .build();
    }

    public static <S, T> PagedResponse<T> from(Page<S> page, Function<S, T> mapper) {
        List<T> mapped = page.getContent().stream().map(mapper).collect(Collectors.toList());
        return from(page, mapped);
    }

    public static <S, T> PagedResponse<T> from(Page<S> page, List<T> mapped) {
        return PagedResponse.<T>builder()
                .data(mapped)
                .meta(PageMeta.builder()
                        .page(page.getNumber())
                        .size(page.getSize())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .build())
                .build();
    }
}
