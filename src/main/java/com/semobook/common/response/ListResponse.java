package com.semobook.common.response;

import com.semobook.common.Meta;
import com.semobook.common.Paging;
import lombok.Builder;
import lombok.Data;

@Data
public class ListResponse<T> {
    private Meta meta;
    private Paging paging;
    private T data;

    @Builder
    public ListResponse(Meta meta, Paging paging, T data) {
        this.meta = meta;
        this.paging = paging;
        this.data = data;
    }
}
