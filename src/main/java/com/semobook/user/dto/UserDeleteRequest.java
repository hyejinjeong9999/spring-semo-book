package com.semobook.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class UserDeleteRequest {

    @NotEmpty
    @Schema(description = "유저 넘버" , example = "1")
    long userNo;
    @NotNull
    @Schema(description = "삭제 사유" , example = "맘에안들어서요")
    String deleteReason;
}

