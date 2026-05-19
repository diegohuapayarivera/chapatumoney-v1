package com.dhr.chapatumoney.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageMeta {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
