package com.semobook.user.service;


import com.semobook.aop.performance.PerformanceCheck;
import com.semobook.bookReview.domain.AllReview;
import com.semobook.bookReview.domain.BookReview;
import com.semobook.bookReview.repository.AllReviewRepository;
import com.semobook.bookReview.repository.BookReviewRepository;
import com.semobook.common.Meta;
import com.semobook.common.Paging;
import com.semobook.common.SemoConstant;
import com.semobook.common.StatusEnum;
import com.semobook.common.response.ListResponse;
import com.semobook.common.response.SingleResponse;
import com.semobook.recom.domain.ReviewInfo;
import com.semobook.tools.SecurityTools;
import com.semobook.tools.StringTools;
import com.semobook.user.domain.UserInfo;
import com.semobook.user.domain.UserPriorityRedis;
import com.semobook.user.domain.UserStatus;
import com.semobook.user.dto.*;
import com.semobook.user.repository.UserPriorityRedisRepository;
import com.semobook.user.repository.UserRepository;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BookReviewRepository bookReviewRepository;
    private final UserPriorityRedisRepository userPriorityRedisRepository;
    private final AllReviewRepository allReviewRepository;
    private final JavaMailSender mailSender;

    /**
     * 모든회원 조회(테스트용)
     *
     * @author hyejinzz
     * @since 2021-05-23
     **/
    public ListResponse findAllUser(int pageNum) {
        String hMessage = "";
        StatusEnum hCode = null;
        List<UserInfoDto> result = null;
        Page<UserInfo> page = null;
        try {
            page = userRepository.findAll(PageRequest.of(pageNum, 5));
            result = page.getContent().stream()
                    .map(r -> new UserInfoDto(r))
                    .collect(Collectors.toList());
            hCode = StatusEnum.hd1004;
            hMessage = "유저 리스트 조회 성공";
        } catch (Exception e) {
            hCode = StatusEnum.hd4444;
            hMessage = "회원조회 실패";
            log.info(":: findAllUser err :: error is {} ", e);
        }

        return ListResponse.builder()
                .meta(Meta.builder().hMessage(hMessage).hCode(hCode).build())
                .paging(Paging.builder().totalPage(page.getTotalPages()).pageNumber(page.getNumber()).totalElements(page.getTotalElements()).build())
                .data(result)
                .build();
    }


    /**
     * userId로 회원조회
     *
     * @author hyejinzz
     * @since 2021-05-23
     **/
    @PerformanceCheck
    public SingleResponse findByUserId(String userId) {
        String hMessage = "";
        StatusEnum hCode = null;
        UserInfoDto userInfoDto = null;
        try {
            userInfoDto = new UserInfoDto(userRepository.findByUserId(userId));
            if (userInfoDto == null) {
                hCode = StatusEnum.hd1004;
                hMessage = "유효하지 않은 회원정보";
            } else {
                hCode = StatusEnum.hd1004;
                hMessage = "회원조회 성공";
            }
        } catch (Exception e) {
            hCode = StatusEnum.hd4444;
            hMessage = "회원조회 실패";
            log.info(":: findByUserId err :: error is {} ", e);
        }

        return SingleResponse.builder()
                .meta(Meta.builder().hMessage(hMessage).hCode(hCode).build())
                .data(userInfoDto)
                .build();
    }


    /**
     * 로그인
     *
     * @author hyejinzz
     * @since 2021-05-23
     **/
    @PerformanceCheck
    public SingleResponse signIn(UserSignInRequest userSignUpRequest) {
        String hMessage = "";
        StatusEnum hCode = null;
        UserInfoDto userInfoDto = null;
        try {
//            UserInfo signUserInfo = userRepository.findByUserIdAndUserStatus(userSignUpRequest.getUserId(), UserStatus.GENERAL);
            UserInfo signUserInfo = userRepository.findByUserId(userSignUpRequest.getUserId());
            userInfoDto = new UserInfoDto(signUserInfo);
            if (signUserInfo == null) {
                hCode = StatusEnum.hd1004;
                hMessage = "없는 USER";
            }
            //id가 있을 때

            else if (!(userSignUpRequest.getUserPassword().equals(signUserInfo.getUserPw()))) {
                hCode = StatusEnum.hd1004;
                hMessage = "로그인 실패";
            } else {
                hCode = StatusEnum.hd1004;
                hMessage = "로그인 성공";
            }
        } catch (Exception e) {
            hCode = StatusEnum.hd4444;
            hMessage = "로그인 실패";
            log.info(":: signIn err :: error is {} ", e);
        }

        return SingleResponse.builder()
                .meta(Meta.builder().hMessage(hMessage).hCode(hCode).build())
                .data(userInfoDto)
                .build();
    }


    /**
     * 회원가입
     *
     * @author hyejinzz
     * @since 2021-05-23
     **/
    @Transactional
    public SingleResponse signUp(UserSignUpRequest userSignUpRequest) {
        String hMessage = "";
        StatusEnum hCode = null;
        UserInfo userInfo = null;
        UserInfo signUserInfo = userRepository.findByUserId(userSignUpRequest.getUserId());
        if (signUserInfo == null) {
            userInfo = userRepository.save(UserInfo.builder()
                    .userId(userSignUpRequest.getUserId())
                    .userPw(userSignUpRequest.getUserPassword())
                    .userName(userSignUpRequest.getUserName())
                    .userGender(userSignUpRequest.getUserGender())
                    .userBirth(userSignUpRequest.getUserBirth())
                    .build());

            hMessage = "생성완료";
            hCode = StatusEnum.hd1004;
        } else {
            hMessage = "이미 있는 아이디";
            hCode = StatusEnum.hd4444;
        }

        return SingleResponse.builder()
                .meta(Meta.builder().hMessage(hMessage).hCode(hCode).build())
                .data(userInfo)
                .build();

    }

    private void validateDuplicateMember(UserSignUpRequest userSignUpRequest){
        UserInfo findUsers = userRepository.findByUserId(userSignUpRequest.getUserId());
        if (findUsers != null){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }


    /**
     * 회원탈퇴
     *
     * @author hyejinzz
     * @since 2021-05-23
     **/
    @Transactional
    public SingleResponse deleteUser(UserDeleteRequest userDeleteRequest) {
        String hMessage = "";
        StatusEnum hCode = null;
        try {
            //기존 유저
            UserInfo user = userRepository.findByUserNoAndUserStatus(userDeleteRequest.getUserNo(), UserStatus.GENERAL);

            if (user == null) {
                hCode = StatusEnum.hd1004;
                hMessage = "조회되는 회원이 없음";
            } else {
                user.delUser(userDeleteRequest.getDeleteReason(), LocalDateTime.now());
                userRepository.save(user);
                hCode = StatusEnum.hd1004;
                hMessage = "탈퇴 성공";
            }
        } catch (Exception e) {
            log.info(":: deleteUser err :: error is {} ", e);
            hCode = StatusEnum.hd4444;
            hMessage = "탈퇴 실패";
        }

        return SingleResponse.builder()
                .meta(Meta.builder().hMessage(hMessage).hCode(hCode).build())
                .build();
    }


    /**
     * 회원정보 수정
     *
     * @author hyejinzz
     * @since 2021-05-23
     **/
    @Transactional
    public SingleResponse updateUser(UserChangeUserInfoRequest updateUser) {
        String hMessage = "";
        StatusEnum hCode = null;
        UserInfoDto userInfoDto = null;
        try {
            //기존 유저
            UserInfo userInfo = userRepository.findByUserNoAndUserStatus(updateUser.getUserNo(), UserStatus.GENERAL);
            if (userInfo == null) {
                hCode = StatusEnum.hd1004;
                hMessage = "조회되는 회원이 없음";
            } else {
                userInfo.changeUserInfo(updateUser);
                userInfoDto = new UserInfoDto(userInfo);

                hCode = StatusEnum.hd1004;
                hMessage = "정보 수정 성공";
            }

        } catch (Exception e) {
            log.info(":: updateUser err :: error is {} ", e);
            hCode = StatusEnum.hd4444;
            hMessage = "정보 수정 실패";
        }

        return SingleResponse.builder()
                .meta(Meta.builder().hMessage(hMessage).hCode(hCode).build())
                .data(userInfoDto)
                .build();
    }


    /**
     * user info with review count api
     *
     * @author hyunho
     * @since 2021/06/24
     **/
    public ListResponse userInfoWithReviewCount(long userNo) {
        String hMessage = "";
        StatusEnum hCode = null;
        UserInfoWithReviewCountDto userInfoWithReviewCountDto = null;
        Page<BookReview> page = null;

        try {
            UserInfo userInfo = userRepository.findByUserNo(userNo);
            page = bookReviewRepository.findAllByUserInfo_userNo(userNo, PageRequest.of(0, 100));
            userInfoWithReviewCountDto = new UserInfoWithReviewCountDto(userInfo.getUserNo(),
                    userInfo.getUserId(),
                    userInfo.getUserName(),
                    page.getTotalElements());

            hCode = StatusEnum.hd1004;
            hMessage = "정보 조회 성공";
        } catch (Exception e) {
            log.info(":: userInfo err :: error is {} ", e);
            hCode = StatusEnum.hd4444;
            hMessage = "정보 조회 실패";
        }

        return ListResponse.builder()
                .meta(Meta.builder().hMessage(hMessage).hCode(hCode).build())
                .paging(Paging.builder().totalPage(page.getTotalPages()).pageNumber(page.getNumber()).totalElements(page.getTotalElements()).build())
                .data(userInfoWithReviewCountDto)
                .build();
    }


    public SingleResponse getUserReviewInfo(long userNo) {
        StatusEnum hCode = null;
        String hMessage = null;
        UserReviewInfo userReviewInfo = null;
        try {
            List<String> list = getUserPriorityList(userNo);
            if (list == null || list.size() < 1) {
                if (makeUserPriority(userNo)) {
                    list = getUserPriorityList(userNo);
                }
            }
            hCode = StatusEnum.hd1004;
            hMessage = "getUserPriority 성공";
            userReviewInfo = getTotalUserReviewInfo(list, userNo);
        } catch (Exception e) {
            hCode = StatusEnum.hd4444;
            hMessage = "getUserPriority 에러";
            log.error(":: getUserPriority err :: error is {} ", e);
        }

        return SingleResponse.builder()
                .meta(Meta.builder().hMessage(hMessage).hCode(hCode).build())
                .data(userReviewInfo)
                .build();
    }

    private UserReviewInfo getTotalUserReviewInfo(List<String> list, long userNo) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.withDayOfMonth(1);
        //이번달 읽은 책
        int bookThisMonth = bookReviewRepository.countfindByBookBetweenDate(userNo, start, now);
        log.info(":: getTotalUserReviewInfo :: bookThisMonth is {} ", bookThisMonth);
        //총 읽은 책
        int bookTotal = bookReviewRepository.countReview(userNo);
        List<String> prioertiesList = new ArrayList<>();
        //유저의 성향
        if (list != null && list.size() > 0) {
            for (String s : list) {
                prioertiesList.add(SemoConstant.CATEGORY_TYPE_MAP.get(s));
            }
        }
        return new UserReviewInfo(bookThisMonth, bookTotal, prioertiesList);

    }


    /**
     * 유저 성향 가져오기 redis
     *
     * @author hyejinzz
     * @since 2021-06-20
     **/
    public List<String> getUserPriorityList(long userId) {
        List<String> userPriority = new ArrayList<>();

        //redis 에서 성향 가져오기
        String value;
        UserPriorityRedis userPriorityRedis = userPriorityRedisRepository.findById(userId).orElse(null);

        if (userPriorityRedis == null || userPriorityRedis.getValue() == null || userPriorityRedis.getValue() == "null") {
            return null;
        }
        if (userPriorityRedis != null) {
            value = userPriorityRedis.getValue();
            userPriority = StringTools.stringConvToList(value, SemoConstant.COLON);

        }
        return userPriority;
    }


    /**
     * 리뷰데이터를 바탕으로 새로운 priority 가져온다
     *
     * @param userNo
     */
    @Transactional
    public boolean makeUserPriority(long userNo) {

        try {
            //redis에서 리뷰 가져오기
            AllReview allReview = allReviewRepository.findById(userNo).orElse(null);
            List<ReviewInfo> value = null;

            if (allReview != null && allReview.getValue() != null) {
                value = allReview.getValue();
            }
            //redis에 값 없으면 db에서 가져오기
            if (allReview == null || allReview.getValue() == null) {
                Page<BookReview> bookReviewList = bookReviewRepository.findAllByUserInfo_userNo(userNo, PageRequest.of(0, 999));
                if (bookReviewList.getTotalPages() == 0 || bookReviewList.getSize() == 0) return false;

                if (bookReviewList != null && bookReviewList.getSize() > 0) {
                    value = bookReviewList.stream().filter(a -> StringUtil.isNullOrEmpty(a.getBook().getCategory()) == false)
                            .map(
                                    a -> ReviewInfo.builder()
                                            .point(a.getRating())
                                            .category(a.getBook().getCategory())
                                            .isbn(a.getBook().getIsbn())
                                            .build()).collect(Collectors.toList());

                    //redis에 리뷰데이터 저장하기
                    allReviewRepository.save(AllReview.builder()
                            .userId(userNo)
                            .value(value)
                            .build());
                }
            }

            //성향 만들기
            if (value != null) {
                //보류하기
//                //4개보다 크면 최신 5개만 가져온다
//                if (value.size() > SemoConstant.CHECK_USER_REVIEW_CNT) {
//                    int cutSize = SemoConstant.CHECK_USER_REVIEW_CNT + 1;
//                    value = value.subList(value.size() - cutSize, value.size());
//                }
                value.stream().filter(a->!(a.getCategory().isEmpty()|| a.getCategory() =="A"));
                Map<String, Integer> map = new HashMap<>();
                for (ReviewInfo reviewInfo : value) {
                    String key = reviewInfo.getCategory();
                    map.put(key, map.getOrDefault(key, 0) + 1);
                }
                List<String> sortKey = new ArrayList<>(map.keySet());
                Collections.sort(sortKey, (o1, o2) -> map.get(o2).compareTo(map.get(o1)));
                //redis 저장
                String resultValue = StringTools.listConvToString(sortKey, SemoConstant.COLON);
                userPriorityRedisRepository.save(UserPriorityRedis.builder()
                        .userNo(userNo)
                        .value(resultValue)
                        .build());
            }
        } catch (Exception e) {
            log.error(":: makeUserPriority err :: error is {} ", e);
            return false;
        }
        return true;
    }


    public SingleResponse mailSend(MailRequest mailRequest) {
        String hMessage = "";
        StatusEnum hCode = null;
        try {

            log.info("test dsata ---- {}", SecurityTools.md5("semo1234"));
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("hyunho058@naver.com");
            message.setFrom("lhyun058@gmail.com");
            message.setSubject(mailRequest.getTitle());
            message.setText(mailRequest.getMessage());

            mailSender.send(message);

            hCode = StatusEnum.hd1004;
            hMessage = "메일 전송 성공";
        } catch (Exception e) {
            log.info(":: userInfo err :: error is {} ", e);
            hCode = StatusEnum.hd4444;
            hMessage = "메일 전송 실패";
        }

        return SingleResponse.builder()
                .meta(Meta.builder().hMessage(hMessage).hCode(hCode).build())
                .data(mailRequest)
                .build();
    }

    /**
     * 비밀번호 찾기 (회원Id[Emial] 검색)
     *
     * @author juhan
     * @since 2021-07-18
     **/
    public SingleResponse findPw(String userId) {
        log.info(":: UserService_findId :: userId is {} ", userId);
        String hMessage = "";
        StatusEnum hCode = null;

        try {
            UserInfo userInfo = userRepository.findByUserId(userId);
            log.info("UserID : {} UserPw : {}", userInfo.getUserId(), userInfo.getUserPw());

            if (userInfo != null){
                String inputPw = tmepCreatePw();

                userInfo.changePw(SecurityTools.md5(inputPw));
                log.info("InputPW : {}", inputPw);
                log.info("lockingPW : {}", userInfo.getUserPw());
                userRepository.save(userInfo);

                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(userId);
                message.setFrom("hyunho058@gmail.com");
                message.setSubject("[SEMO BOOK]" + userInfo.getUserName() + "  님 비밀번호 변경 건.");
                message.setText("비밀번호는" + inputPw + " 로 변경완료.");

                mailSender.send(message);

                hCode = StatusEnum.hd1004;
                hMessage = "정보 조회 성공";
            }else {
                hCode = StatusEnum.hd1004;
                hMessage = "아이디를 다시 확인해주세요.";
            }
        } catch (Exception e) {
            log.info(":: userInfo err :: error is {} ", e);
            hCode = StatusEnum.hd4444;
            hMessage = "정보 조회 실패";
        }

        return SingleResponse.builder()
                .meta(Meta.builder().hMessage(hMessage).hCode(hCode).build())
                .build();


    }

    /**
     * 임시 비밀번호 난수생성
     *
     * @author juhan
     * @since 2021-07-18
     **/
    public String tmepCreatePw(){
        log.info(":: UserService_tempCreatePw ::");

        StringBuffer sb = null;
        try {
            char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                    'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
                    'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
                    'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '!', '@',
                    '#', '$', '%', '^', '&' };
            sb = new StringBuffer();
            SecureRandom sr = new SecureRandom();
            sr.setSeed(new Date().getTime());

            int idx = 0;
            int len = charSet.length;
            for(int i = 0; i < SemoConstant.TEMP_PW; i++){
                idx = sr.nextInt(len);
                sb.append(charSet[idx]);
            }

        }catch (Exception e){
            log.info(":: userInfo err :: error is {} ", e);
        }

        return sb.toString();
    }

}
