package com.semobook.user.dto;

import com.semobook.bookReview.dto.BookReviewDto;
import com.semobook.user.domain.UserInfo;
import com.semobook.user.domain.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserInfoListDto {

    private long userNo;
    private String userId;
    private String userPw;
    private UserStatus userStatus;
    private String userName;
    private String userGender;
    private String userBirth;
    private LocalDateTime lastConnection;
    private List<BookReviewDto> bookReviewDtoList;

    public UserInfoListDto(UserInfo userInfo) {
        this.userNo = userInfo.getUserNo();
        this.userId = userInfo.getUserId();
        this.userPw = userInfo.getUserPw();
        this.userStatus = userInfo.getUserStatus();
        this.userName = userInfo.getUserName();
        this.userGender = userInfo.getUserGender();
        this.userBirth = userInfo.getUserBirth();
        this.lastConnection = userInfo.getLastConnection();
        this.bookReviewDtoList = userInfo.getBookReviews().stream()
                .map(r->new BookReviewDto(r))
                .collect(Collectors.toList());
    }

}