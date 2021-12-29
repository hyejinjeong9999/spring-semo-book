package com.semobook.user.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.semobook.user.domain.UserInfo;
import com.semobook.user.domain.UserStatus;
import com.semobook.user.dto.QUserInfoDto;
import com.semobook.user.dto.UserInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.semobook.bookwant.domain.QBookWant.bookWant;
import static com.semobook.user.domain.QUserInfo.userInfo;


public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<UserInfo> findAll(Pageable pageable) {
        List<UserInfo> results = queryFactory
                .selectFrom(userInfo)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .selectFrom(userInfo)
                .fetchCount();
        return new PageImpl<>(results, pageable, totalCount);
    }


    @Override
    public UserInfo findByUserNoAndUserStatus(long userNo, Enum<UserStatus> status) {
        return queryFactory
                .selectFrom(userInfo)
                .where(userNoEq(userNo), userInfo.userStatus.eq((UserStatus) status))
                .fetchOne();
    }

    @Override
    public UserInfo findByUserId(String userId) {
        return queryFactory
                .selectFrom(userInfo)
                .where(userIdEq(userId))
                .fetchOne();
    }

    //@Query("select u from UserInfo u left join fetch u.bookReviews br where u.userNo = :userNo")
    @Override
    public UserInfo findByUserNo(long userNo) {
        return queryFactory
                .selectFrom(userInfo)
                .leftJoin(userInfo.bookReviews).fetchJoin()
                .where(userNoEq(userNo))
                .fetchOne();
    }

    @Override
    public UserInfo findByBookWantWithReview(long userNo) {
        return queryFactory
                .selectFrom(userInfo)
                .join(userInfo.bookWants, bookWant).fetchJoin()
                .join(bookWant.book).fetchJoin()
                .where(userNoEq(userNo))
                .fetchOne();
    }

    @Override
    public Page<UserInfoDto> findAllDtoByProjection(Pageable pageable) {
        List<UserInfoDto> results = queryFactory
                .select(Projections.constructor(UserInfoDto.class,
                        userInfo.userNo,
                        userInfo.userId,
                        userInfo.userStatus,
                        userInfo.userName,
                        userInfo.userGender,
                        userInfo.userBirth,
                        userInfo.lastConnection,
                        userInfo.userPriority))
                .from(userInfo)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .selectFrom(userInfo)
                .fetchCount();
        return new PageImpl<>(results, pageable, totalCount);
    }

    @Override
    public Page<UserInfoDto> findAllDtoByQueryProjection(Pageable pageable) {
        List<UserInfoDto> results = queryFactory
                .select(new QUserInfoDto(userInfo.userId, userInfo.userName))
                .from(userInfo)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .selectFrom(userInfo)
                .fetchCount();
        return new PageImpl<>(results, pageable, totalCount);
    }


    private BooleanExpression userNoEq(Long userNo) {
        return userNo != null ? userInfo.userNo.eq(userNo) : null;
    }

    private BooleanExpression userIdEq(String userId) {
        return userId != null ? userInfo.userId.eq(userId) : null;
    }

}
