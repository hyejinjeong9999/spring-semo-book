package com.semobook.test;

import com.semobook.user.domain.UserInfo;
import com.semobook.user.dto.UserInfoDto;
import com.semobook.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
public class UserInfoTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("FIND_ALL_USER")
    void FIND_ALL_USER(){
        //give
        UserInfo userA = UserInfo.builder()
                .userNo(99999L)
                .userId("userA@semo.com")
                .userPw("semo1234")
                .userName("userA")
                .userGender("M")
                .userBirth("19920519")
                .build();
        UserInfo userB = UserInfo.builder()
                .userNo(99998L)
                .userId("userB@semo.com")
                .userPw("semo1234")
                .userName("userB")
                .userGender("M")
                .userBirth("19920519")
                .build();
        UserInfo userC = UserInfo.builder()
                .userNo(99997L)
                .userId("userC@semo.com")
                .userPw("semo1234")
                .userName("userC")
                .userGender("M")
                .userBirth("19920519")
                .build();
        //when
        userRepository.save(userA);
        userRepository.save(userB);
        userRepository.save(userC);
        //then
        Page<UserInfo> page = userRepository.findAll(PageRequest.of(0, 2));
        List<UserInfoDto> results = page.getContent().stream()
                .map(r -> new UserInfoDto(r))
                .collect(Collectors.toList());

        assertThat(results.size(), is(2));
        assertThat(results.get(0).getUserName(), is("userA"));
        assertThat(page.getTotalElements(), is(3L));
        assertThat(page.getTotalPages(), is(2));

    }

    @Test
    @DisplayName("사용자_아이디로_조회")
    void 사용자_아이디로_조회(){
        //give
        UserInfo userA = UserInfo.builder()
                .userNo(99999L)
                .userId("userA@semo.com")
                .userPw("semo1234")
                .userName("userA")
                .userGender("M")
                .userBirth("19920519")
                .build();
        UserInfo userB = UserInfo.builder()
                .userNo(99998L)
                .userId("userB@semo.com")
                .userPw("semo1234")
                .userName("userB")
                .userGender("M")
                .userBirth("19920519")
                .build();
        UserInfo userC = UserInfo.builder()
                .userNo(99997L)
                .userId("userC@semo.com")
                .userPw("semo1234")
                .userName("userC")
                .userGender("M")
                .userBirth("19920519")
                .build();
        //when
        userRepository.save(userA);
        userRepository.save(userB);
        userRepository.save(userC);
        //then
        UserInfo userInfo = userRepository.findByUserId("userB@semo.com");
        assertThat(userInfo.getUserId(), is(userB.getUserId()));
    }
}
