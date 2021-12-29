package com.semobook.test;

import com.semobook.book.domain.Book;
import com.semobook.book.repository.BookRepository;
import com.semobook.bookReview.service.BookReviewService;
import com.semobook.bookwant.domain.BookWant;
import com.semobook.bookwant.dto.BookWantDto;
import com.semobook.bookwant.dto.Preference;
import com.semobook.bookwant.repository.BookWantRepository;
import com.semobook.user.domain.UserInfo;
import com.semobook.user.domain.UserStatus;
import com.semobook.user.dto.UserInfoDto;
import com.semobook.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
public class UserInfoTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    BookReviewService bookReviewService;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    BookWantRepository bookWantRepository;

    @Test
    @DisplayName("FIND_ALL_USER")
    void FIND_ALL_USER(){
        //given
        UserInfo userA = UserInfo.builder()
                .userId("userA@semo.com")
                .userPw("semo1234")
                .userName("userA")
                .userGender("M")
                .userBirth("19920519")
                .build();
        UserInfo userB = UserInfo.builder()
                .userId("userB@semo.com")
                .userPw("semo1234")
                .userName("userB")
                .userGender("M")
                .userBirth("19920519")
                .build();
        UserInfo userC = UserInfo.builder()
                .userId("userC@semo.com")
                .userPw("semo1234")
                .userName("userC")
                .userGender("M")
                .userBirth("19920519")
                .build();
        userRepository.save(userA);
        userRepository.save(userB);
        userRepository.save(userC);
        //when
        Page<UserInfo> page = userRepository.findAll(PageRequest.of(0, 2));
        List<UserInfoDto> results = page.getContent().stream()
                .map(r -> new UserInfoDto(r))
                .collect(Collectors.toList());
        //then
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0).getUserName()).isEqualTo("userA");
        assertThat(page.getTotalElements()).isEqualTo(3L);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("사용자_아이디로_조회")
    void 사용자_아이디로_조회(){
        //given
        UserInfo userA = UserInfo.builder()
                .userId("userA@semo.com")
                .userPw("semo1234")
                .userName("userA")
                .userGender("M")
                .userBirth("19920519")
                .build();
        UserInfo userB = UserInfo.builder()
                .userId("userB@semo.com")
                .userPw("semo1234")
                .userName("userB")
                .userGender("M")
                .userBirth("19920519")
                .build();
        UserInfo userC = UserInfo.builder()
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
        assertThat(userInfo.getUserId()).isEqualTo(userB.getUserId());
    }

    @Test
    @DisplayName("유저_도서_좋아요_리스트")
    void 유저_도서_좋아요_리스트(){
        //given
        String userId = "userA@semo.com";
        String isbn = "11111111";

        UserInfo userA = UserInfo.builder()
                .userId(userId)
                .userPw("semo1234")
                .userName("userA")
                .userGender("M")
                .userBirth("19920519")
                .build();
        userRepository.save(userA);
//        UserInfo userData = userRepository.findByUserId(userId);

        Book book = bookRepository.save(Book.builder()
                .isbn(isbn)
                .bookName("SEMO")
                .author("SEMO")
                .publisher("hDream")
                .kdc("800")
                .category("800")
                .keyword("800")
                .img("http://image.kyobobook.co.kr/images/book/large/924/l9788901214924.jpg")
                .build());
        bookRepository.save(book);

        BookWant bookWant = BookWant.builder()
                .book(book)
                .preference(Preference.LIKE)
                .userInfo(userA)
                .build();

        userA.addBoonWand(bookWant);
        bookWantRepository.save(bookWant);
        //when
        Page<BookWant> bookWantPage = bookWantRepository.findLikeAllByUserInfo(userA.getUserNo(), Preference.LIKE, PageRequest.of(0, 10));
        List<BookWantDto> bookWants = bookWantPage.stream().map(bw -> new BookWantDto(bw)).collect(Collectors.toList());
        //then
        assertThat(userA.getUserName()).isEqualTo(userA.getUserName());
        assertThat(bookWantPage.getTotalElements()).isEqualTo(1);
        assertThat(bookWants.get(0).getIsbn()).isEqualTo(isbn);
        assertThat(bookWants.get(0).getPreference()).isEqualTo(Preference.LIKE);
    }

    @Test
    @DisplayName("유저_상태별_검색")
    void 유저_상태별_검색(){
        //given
        String userId = "userA@semo.com";
        UserInfo userA = UserInfo.builder()
                .userId(userId)
                .userPw("semo1234")
                .userName("userA")
                .userGender("M")
                .userBirth("19920519")
                .build();
        //when
        userRepository.save(userA);
        System.out.println("userA.getUserNo() = " + userA.getUserNo());
        UserInfo generalUser = userRepository.findByUserNoAndUserStatus(userA.getUserNo(), UserStatus.GENERAL);
        UserInfo userTest = userRepository.findByUserNoAndUserStatus(userA.getUserNo(), UserStatus.DELETE);

        //then
        assertThat(generalUser.getUserId()).isEqualTo(userA.getUserId());
        assertThat(userTest).isNull();
    }

    @Test
    @DisplayName("BaseTimeEntity_등록")
    void BaseTimeEntity_등록(){
        //given
        LocalDateTime now = LocalDateTime.of(2021,10,06,0,0,0);
        String userId = "userQ@semo.com";
        UserInfo userQ = UserInfo.builder()
                .userId("userQ@semo.com")
                .userPw("semo1234")
                .userName("userQ")
                .userGender("M")
                .userBirth("19920519")
                .build();
        //when
        userRepository.save(userQ);
        UserInfo resultUser = userRepository.findByUserId(userId);
        //then
        System.out.println("now = " + now);
        System.out.println("------------->>> createDate="+resultUser.getModifiedDate()+", modifiedDate="+resultUser.getModifiedDate());

        assertThat(resultUser.getCreateDate()).isAfter(now);
        assertThat(resultUser.getModifiedDate()).isAfter(now);
    }


    @Test
    @DisplayName("유정_정보_수정")
    void 유정_정보_수정(){
        //given
        LocalDateTime now = LocalDateTime.of(2021,10,06,0,0,0);
        long userNo = 989898;
        String userId = "userQ@semo.com";
        UserInfo userQ = UserInfo.builder()
                .userNo(userNo)
                .userId(userId)
                .userPw("semo0000")
                .userName("userQ")
                .userGender("M")
                .userBirth("19920519")
                .build();
        //when
        userRepository.save(userQ);
        UserInfo resultUser = userRepository.findByUserId(userId);
        //then
        System.out.println("now = " + now);
        System.out.println("Create------------->>> createDate="+resultUser.getModifiedDate()+", modifiedDate="+resultUser.getModifiedDate());

        assertThat(resultUser.getCreateDate()).isAfter(now);
        assertThat(resultUser.getModifiedDate()).isAfter(now);
    }

    @Test
    @DisplayName("FIND_ALL_DTO_BY_PROJECTION")
    void FIND_ALL_DTO_BY_PROJECTION(){
        //given
        UserInfo userA = UserInfo.builder()
                .userId("userA@semo.com")
                .userPw("semo1234")
                .userName("userA")
                .userGender("M")
                .userBirth("19920519")
                .build();
        UserInfo userB = UserInfo.builder()
                .userId("userB@semo.com")
                .userPw("semo1234")
                .userName("userB")
                .userGender("M")
                .userBirth("19920519")
                .build();
        UserInfo userC = UserInfo.builder()
                .userId("userC@semo.com")
                .userPw("semo1234")
                .userName("userC")
                .userGender("M")
                .userBirth("19920519")
                .build();
        userRepository.save(userA);
        userRepository.save(userB);
        userRepository.save(userC);
        //when
        Page<UserInfoDto> results = userRepository.findAllDtoByProjection(PageRequest.of(0, 2));
//        List<UserInfoDto> results = page.getContent().stream()
//                .map(r -> new UserInfoDto(r))
//                .collect(Collectors.toList());
        //then
        assertThat(results.getContent().size()).isEqualTo(2);
        assertThat(results.getContent().get(0).getUserName()).isEqualTo("userA");
        assertThat(results.getTotalElements()).isEqualTo(3L);
        assertThat(results.getTotalPages()).isEqualTo(2);
    }
}
