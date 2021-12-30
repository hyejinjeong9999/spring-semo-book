package com.semobook.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;


@Getter
@NoArgsConstructor
public class UserChangeUserInfoRequest {
    @NotEmpty
    @Schema(description = "유저No" , example = "12")
    long userNo;
    @NotEmpty
    @Schema(description = "유저패스워드" , example = "0")
    String userPassword;
    @NotEmpty
    @Schema(description = "유저이름" , example = "0")
    String userName;
    @NotEmpty
    @Schema(description = "유저성별" , example = "W")
    String userGender;
    @Pattern(regexp="([0-9]{2}(0[1-9]|1[0-2])(0[1-9]|[1,2][0-9]|3[0,1]))", message = "생년월일은 6자리로 입력해주세요.")
    @Schema(description = "유저생일" , example = "940810")
    String userBirth;

}
