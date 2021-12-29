package com.semobook.user.repository;

import com.semobook.user.domain.UserInfo;
import com.semobook.user.domain.UserStatus;
import com.semobook.user.dto.UserInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {
    //모든 유저 찾기
    Page<UserInfo> findAll(Pageable pageable);

    //유저id로 회원조회 찾기 (휴먼, 정지, 탈퇴 제외)
    UserInfo findByUserNoAndUserStatus(long userNo, Enum<UserStatus> status);

    //회원조회(모두)
    UserInfo findByUserId(String userId);

    //find user by userNo
    UserInfo findByUserNo(long userNo);

    //유저별 원하는 책 가져오기
    UserInfo findByBookWantWithReview(long userNo);

    Page<UserInfoDto> findAllDtoByProjection(Pageable pageable);

    Page<UserInfoDto> findAllDtoByQueryProjection(Pageable pageable);
}
