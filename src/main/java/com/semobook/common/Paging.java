package com.semobook.common;

import lombok.Builder;
import lombok.Data;

@Data
public class Paging {
    private int totalPage;
    private long totalElements;
    private int pageNumber;

    @Builder
    public Paging(int totalPage, long totalElements, int pageNumber) {
        this.totalPage = totalPage;
        this.totalElements = totalElements;
        this.pageNumber = pageNumber;
    }
}
