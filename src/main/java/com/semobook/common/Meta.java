package com.semobook.common;

import lombok.Builder;
import lombok.Data;

@Data
public class Meta {
    private StatusEnum hCode;
    private String hMessage;

    @Builder
    public Meta(StatusEnum hCode, String hMessage) {
        this.hCode = hCode;
        this.hMessage = hMessage;
    }
}
