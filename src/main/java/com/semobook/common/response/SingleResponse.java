package com.semobook.common.response;

import com.semobook.common.Meta;
import lombok.Builder;
import lombok.Data;

@Data
public class SingleResponse<T> {
    private Meta meta;
    private T data;

    @Builder
    public SingleResponse(Meta meta, T data) {
        this.meta = meta;
        this.data = data;
    }
}
