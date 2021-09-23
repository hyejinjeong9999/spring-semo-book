package com.semobook.test;

import com.semobook.book.domain.Book;
import com.semobook.book.dto.BookDto;
import com.semobook.book.dto.BookWithReviewDto;
import com.semobook.book.repository.BookRepository;
import com.semobook.book.service.BestSellerService;
import com.semobook.book.service.BookService;
import com.semobook.bookReview.dto.BookReviewRequest;
import com.semobook.bookReview.repository.BookReviewRepository;
import com.semobook.bookReview.service.BookReviewService;
import com.semobook.user.domain.UserInfo;
import com.semobook.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
public class BookTest {
    @Autowired
    BestSellerService bestSellerService;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    BookReviewRepository bookReviewRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BookReviewService bookReviewService;
    @Autowired
    BookService bookService;



    @Test
    @DisplayName("도서_검색_ISBN")
    void 도서_검색_ISBN(){
        //give
        String isbn = "11111111";
        Book book = (Book.builder()
                .isbn(isbn)
                .bookName("SEMO")
                .author("SEMO")
                .publisher("hDream")
                .kdc("800")
                .category("800")
                .keyword("800")
                .img("http://image.kyobobook.co.kr/images/book/large/924/l9788901214924.jpg")
                .build());
        //when
        bookRepository.save(book);
        Book resultBook = bookRepository.findByIsbn(isbn);
        //then
        assertThat(resultBook.getIsbn()).isEqualTo(isbn);
    }

    @Test
    @DisplayName("도서_리뷰조회_ISBN")
    void 도서_리뷰조회_ISBN(){
        //give
        long userNoA = 1;
        long userNoB = 2;
        String isbn = "222221";

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
                .userGender("W")
                .userBirth("19920519")
                .build();

        Book book = (Book.builder()
                .isbn(isbn)
                .bookName("SEMO")
                .author("SEMO")
                .publisher("hDream")
                .kdc("800")
                .category("800")
                .keyword("800")
                .img("http://image.kyobobook.co.kr/images/book/large/924/l9788901214924.jpg")
                .build());

        BookReviewRequest rq1 = BookReviewRequest.builder()
                .userNo(userNoA)
                .isbn(isbn)
                .rating(4)
                .reviewContents("재미")
                .book(new BookDto(book))
                .build();

        BookReviewRequest rq2 = BookReviewRequest.builder()
                .userNo(userNoB)
                .isbn(isbn)
                .rating(3)
                .reviewContents("재미11")
                .book(new BookDto(book))
                .build();
        //when
        userRepository.save(userA);
        userRepository.save(userB);
        bookRepository.save(book);
        bookReviewService.createReview(rq1);
        bookReviewService.createReview(rq2);

        Book testBook = bookRepository.findByIsbn(isbn);
        System.out.println("khh test testBook.getIsbn() = " + testBook.getIsbn());
        UserInfo testUserA = userRepository.findByUserNo(userNoA);
        System.out.println("khh test testUserA = " + testUserA.getUserNo());
        UserInfo testUserB= userRepository.findByUserNo(userNoB);
        System.out.println("khh test testUserB = " + testUserB.getUserNo());

        BookWithReviewDto bookWithReviewDto = new BookWithReviewDto(bookRepository.findByIsbnWithReview(isbn));;
        System.out.println("bookWithReviewDto.getBookName() = " + bookWithReviewDto.getBookName());
        System.out.println("bookWithReviewDto.getBookReviews().size() = " + bookWithReviewDto.getBookReviews().size());

        //then
        assertThat(bookWithReviewDto.getIsbn()).isEqualTo(isbn);
        assertThat(bookWithReviewDto.getBookReviews().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("도서_리스트")
    void 도서_리스트(){
        //give
        int isbn = 11111111;
        for (int i = 0; i < 11; i++){
            Book book = Book.builder()
                    .isbn(String.valueOf(isbn++))
                    .bookName("SEMO"+i)
                    .author("SEMO")
                    .publisher("hDream")
                    .kdc("800")
                    .category("800")
                    .keyword("800")
                    .img("http://image.kyobobook.co.kr/images/book/large/924/l9788901214924.jpg")
                    .build();
            bookRepository.save(book);
        }
        //when
        int pageNum = 0;
        int pageSize = 5;
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        Page<Book> page = bookRepository.findAll(pageRequest);
        //then

//        assertThat(page.getTotalElements(), is(11L));
//        assertThat(page.getTotalPages(), is(3));
//        assertThat(page.isFirst(), is(true));

        assertThat(page.getTotalElements()).isEqualTo(11L);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.isFirst()).isEqualTo(true);
    }

    @Test
    @DisplayName("도서_내용_수정")
    void 도서_내용_수정(){
        //give
        String isbn = "11111111";
        Book book = bookRepository.save(Book.builder()
                .isbn(isbn)
                .bookName("SEMO")
                .author("SEMO")
                .publisher("hDream")
                .contents("semosemo")
                .kdc("800")
                .category("800")
                .keyword("800")
                .img("http://image.kyobobook.co.kr/images/book/large/924/l9788901214924.jpg")
                .build());
        //when
        String updateContents = "ABC";
        bookService.updateBookContents(isbn, updateContents);
        //then
        Book updateBookData = bookRepository.findByIsbn(isbn);
        assertThat(updateBookData.getContents()).isEqualTo(updateContents);
    }

    @Test
    @DisplayName("베스트셀러를_가져온다")
    void BEST_SELLSR_TEST() {
        List<BookDto> listA = bestSellerService.getBestSellerList("A", 10);
        listA.forEach(System.out::println);
        List<BookDto> list100 = bestSellerService.getBestSellerList("100", 10);
        list100.forEach(System.out::println);
        List<BookDto> list200 = bestSellerService.getBestSellerList("200", 10);
        list200.forEach(System.out::println);
    }

    @Test
    @DisplayName("스테디셀러를_가져온다")
    void STEADY_SELLER_TEST() throws Exception {
        List<BookDto> listA = bestSellerService.getSteadySellerList("A", 10);
        listA.forEach(System.out::println);
        List<BookDto> list100 = bestSellerService.getSteadySellerList("100", 10);
        list100.forEach(System.out::println);
        List<BookDto> list200 = bestSellerService.getSteadySellerList("200", 10);
        list200.forEach(System.out::println);
    }
}
