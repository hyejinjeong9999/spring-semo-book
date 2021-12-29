package com.semobook.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.semobook.user.domain.UserInfo;
import com.semobook.user.domain.UserStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserInfoDto {
    private long userNo;
    private String userId;
    private UserStatus userStatus;
    private String userName;
    private String userGender;
    private String userBirth;
    private LocalDateTime lastConnection;
    private String userPriority;

    @QueryProjection
    public UserInfoDto(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public UserInfoDto(UserInfo userInfo) {
        this.userNo = userInfo.getUserNo();
        this.userId = userInfo.getUserId();
        this.userStatus = userInfo.getUserStatus();
        this.userName = userInfo.getUserName();
        this.userGender = userInfo.getUserGender();
        this.userBirth = userInfo.getUserBirth();
        this.lastConnection = userInfo.getLastConnection();
        this.userPriority = userInfo.getUserPriority();
    }
}
