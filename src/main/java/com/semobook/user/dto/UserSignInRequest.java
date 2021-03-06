package com.semobook.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;


@Getter
@NoArgsConstructor
public class UserSignInRequest {
    @NotEmpty
    @Email(message = "이메일 형식에 맞지 않습니다.")
    @Schema(description = "유저아이디" , example = "0")
    String userId;
    @NotEmpty
    @Schema(description = "유저패스워드" , example = "0")
    String userPassword;
    @Schema(hidden = true)
    String userStatus;

}
