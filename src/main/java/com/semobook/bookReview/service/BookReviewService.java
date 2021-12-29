package com.semobook.bookReview.service;

import com.semobook.book.domain.Book;
import com.semobook.book.dto.BookDto;
import com.semobook.book.repository.BookRepository;
import com.semobook.book.service.BookService;
import com.semobook.bookReview.domain.AllReview;
import com.semobook.bookReview.domain.BookReview;
import com.semobook.bookReview.dto.*;
import com.semobook.bookReview.dto.request.MonthBookReviewRequest;
import com.semobook.bookReview.dto.request.SearchBookReviewDto;
import com.semobook.bookReview.repository.AllReviewRepository;
import com.semobook.bookReview.repository.BookReviewRepository;
import com.semobook.common.StatusEnum;
import com.semobook.recom.domain.ReviewInfo;
import com.semobook.recom.service.UserRandomEvaluation;
import com.semobook.user.domain.UserInfo;
import com.semobook.user.repository.UserRepository;
import com.semobook.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 리뷰 서비스
 *
 * @author hjjung
 * @since 2021-05-16
 **/
@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class BookReviewService {

    private final BookReviewRepository bookReviewRepository;
    private final UserRandomEvaluation userRandomEvaluation;
    private final BookService bookService;
    private final UserService userService;
    private final AllReviewRepository allReviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;


    /**
     * 글 등록
     *
     * @author hyejinzz
     * @since 2021/05/29
     **/
    @Transactional
    public BookReviewResponse createReview(BookReviewRequest request) {
        log.info("createReview ::");
        String hMessage;
        StatusEnum hCode;
        log.info("createReview():: request.getUserNo() is {}", request.getUserNo());
        log.info("createReview():: request.getBook().getIsbn() is {}", request.getBook().getIsbn());
        try {
            if (bookReviewRepository.exists(request.getUserNo(), request.getBook().getIsbn())) {
                log.info("createReview():: review is existence");
                hCode = StatusEnum.hd1004;
                hMessage = "이미 리뷰를 등록하였습니다.";
            } else {
                log.info("createReview():: review is not existence");
                Book book;
                if (bookRepository.existsByIsbn(request.getBook().getIsbn())) {
                    log.info("createReview():: book is existence");
                    book = bookRepository.findByIsbn(request.getBook().getIsbn());
                } else {
                    log.info("createReview():: book is not existence");
                    book = bookRepository.save(Book.builder()
                            .isbn(request.getBook().getIsbn())
                            .bookName(request.getBook().getBookName())
                            .author(request.getBook().getAuthor())
                            .publisher(request.getBook().getPublisher())
                            .kdc(request.getBook().getKdc())
                            .category(request.getBook().getCategory())
                            .keyword(request.getBook().getKeyword())
                            .img(request.getBook().getImg())
                            .build());
                }
                UserInfo resultUserInfo = userRepository.findByUserNo(request.getUserNo());
                if (book != null && resultUserInfo != null) {
                    bookReviewRepository.save(BookReview.builder()
                            .rating(request.getRating())
                            .reviewContents(request.getReviewContents())
                            .createDate(LocalDateTime.now())
                            .declaration(0)
                            .book(book)
                            .userInfo(resultUserInfo)
                            .build());

                    //redis에 리뷰 업데이트
                    boolean isUpdate = updateRedisReview(request);

                    //userpriority 생성
                    if (isUpdate) {
                        userService.makeUserPriority(request.getUserNo());
                    }

                    //평점  3점 이상이면 recom으로 추천 업뎃치기
//                    if (request.getRating() >= 3) {
//                    recomService.updateUserReviewRecom(request.getIsbn(),request.getUserNo());
//                    }
                    //레디스에
                    hCode = StatusEnum.hd1004;
                    hMessage = "저장완료";
                    log.info("createReview:: success create book review");
                } else {
                    hCode = StatusEnum.hd4444;
                    hMessage = "저장실패";
                }

            }

        } catch (Exception e) {
            log.error("createReview err :: error msg : {}", e);
            hCode = StatusEnum.hd4444;
            hMessage = "createReview 에러";
        }

        return BookReviewResponse.builder()
                .hCode(hCode)
                .hMessage(hMessage)
                .build();
    }

    /**
     * redis에 리뷰 update
     *
     * @author hyejinzz
     * @since 2021/07/11
     **/
    private boolean updateRedisReview(BookReviewRequest request) {
        //책정보 조회
        try {
            BookDto bookDto = bookService.findBook3Step(request.getBook().getIsbn());
            List<ReviewInfo> reviewInfoList = new ArrayList<>();
            //카테고리 정보가 없으면 업데이트하지 못함
            if (bookDto.getCategory() == null) return false;
            //redis에서 기존 리뷰데이터 꺼내서 새로 setting
            AllReview allReviewList = allReviewRepository.findById(request.getUserNo()).orElse(null);
            if (allReviewList != null) {
                reviewInfoList = allReviewList.getValue();

            }
            //redis값이 없으면 db에서 가져오기
            if (allReviewList == null) {
                reviewInfoList = bookReviewRepository.findAllByUserInfo_userNo(request.getUserNo(), PageRequest.of(0, 100))
                        .stream().filter(a -> !(a.getBook().getCategory().isEmpty() || a.getBook().getCategory() == "A"))
                        .map(a -> ReviewInfo.builder()
                                .point(a.getRating())
                                .isbn(a.getBook().getIsbn())
                                .category(a.getBook().getCategory())
                                .build()).collect(Collectors.toList());

            }
            //redis에 저장
            reviewInfoList.add(ReviewInfo.builder()
                    .category(bookDto.getCategory())
                    .isbn(bookDto.getIsbn())
                    .point(request.getRating())
                    .build());

            if (reviewInfoList.size() > 0) {
                AllReview saveData = AllReview.builder().userId(request.getUserNo()).value(reviewInfoList).build();
                allReviewRepository.save(saveData);

            }
        } catch (Exception e) {
            log.error(":: updateRedisReview err :: error is {} ", e);
            return false;
        }
        return true;
    }


    /**
     * 도서 별점 주기
     *
     * @author hyunho
     * @since 2021/06/13
     **/
    @Transactional
    public BookReviewResponse bookReviewRating(BookReviewRatingRequest request) {
        String hMessage = null;
        StatusEnum hCode = null;
        try {
            if (bookReviewRepository.exists(request.getUserNo(), request.getBook().getIsbn())) {
                log.info("bookReviewRating:: review is existence");
            } else {
                log.info("bookReviewRating:: review is not existence");
                Book book;
                if (bookRepository.existsByIsbn(request.getBook().getIsbn())) {
                    log.info("bookReviewRating:: book is existence");
                    book = bookRepository.findByIsbn(request.getBook().getIsbn());
                } else {
                    log.info("bookReviewRating:: book is not existence");
                    book = bookRepository.save(Book.builder()
                            .isbn(request.getBook().getIsbn())
                            .bookName(request.getBook().getBookName())
                            .author(request.getBook().getAuthor())
                            .publisher(request.getBook().getPublisher())
                            .kdc(request.getBook().getKdc())
                            .category(request.getBook().getCategory())
                            .keyword(request.getBook().getKeyword())
                            .img(request.getBook().getImg())
                            .build());
                }
                UserInfo resultUserInfo = userRepository.findByUserNo(request.getUserNo());
                log.info("createReview :: resultUserInfo is {}", resultUserInfo.getUserName());
                if (book != null && resultUserInfo != null) {
                    bookReviewRepository.save(BookReview.builder()
                            .rating(request.getRating())
                            .createDate(LocalDateTime.now())
                            .declaration(0)
                            .book(book)
                            .userInfo(resultUserInfo)
                            .build());
                    //redis에 리뷰 업데이트
                    boolean isUpdate = updateRedisReview(BookReviewRequest.builder()
                            .userNo(request.getUserNo())
                            .rating(request.getRating())
                            .book(request.getBook())
                            .build());

                    //userpriority 생성
                    if (isUpdate) {
                        userService.makeUserPriority(request.getUserNo());
                    }

                    hCode = StatusEnum.hd1004;
                    hMessage = "저장완료";
                } else {
                    hCode = StatusEnum.hd4444;
                    hMessage = "저장실패";
                }
            }


        } catch (Exception e) {
            log.error("createReview err :: error msg : {}", e);
            hCode = StatusEnum.hd4444;
            hMessage = "createReview 에러";
        }

        return BookReviewResponse.builder()
                .hCode(hCode)
                .hMessage(hMessage)
                .build();
    }

    /**
     * 내 글 보여주기
     *
     * @author hyejinzz
     * @since 2021-05-29
     **/
    public BookReviewResponse readMyReview(long userNo, int pageNum) {
        log.info(":: readMyReview() :: userNo is {}", userNo);
        String hMessage = "";
        StatusEnum hCode = null;
        List<BookReviewWithIsbnDto> allReview = null;

        try {

            Page<BookReview> page = bookReviewRepository.findAllByUserInfo_userNo(userNo, PageRequest.of(pageNum, 5));
            allReview = page.getContent().stream()
                    .map(bookReview -> new BookReviewWithIsbnDto(bookReview))
                    .collect(Collectors.toList());
            log.info("readMyReview :: count is {}", page.getTotalElements());

            hCode = StatusEnum.hd1004;
            hMessage = "가져오기";

        } catch (Exception e) {
            log.error("createReview err :: error msg : {}", e);
            hCode = StatusEnum.hd4444;
            hMessage = "readMyReview 에러";

        }

        return BookReviewResponse.builder()
                .data(allReview)
                .hCode(hCode)
                .hMessage(hMessage)
                .build();
    }


    /**
     * 모든 글
     * reference - https://www.inflearn.com/questions/14559
     *
     * @author hyejinzz, hyunho
     * @since 2021/05/30
     **/
    public BookReviewResponse readReviewAll(int pageNum) {
        log.info("showReview");
        String hMessage = null;
        StatusEnum hCode = null;
        List<BookReviewWithBookDto> result = null;
        try {
            Page<BookReview> page = bookReviewRepository.findAll(PageRequest.of(pageNum, 5));
            result = page.getContent().stream()
                    .map(bookReview -> new BookReviewWithBookDto(bookReview))
                    .collect(Collectors.toList());
            log.info("bookReviewList : {}", result.toString());

            hCode = StatusEnum.hd1004;
            hMessage = "가져오기";

        } catch (Exception e) {
            log.error("createReview err :: error msg : {}", e);
            hCode = StatusEnum.hd4444;
            hMessage = "readReview 에러";
        }

        return BookReviewResponse.builder()
                .data(result)
                .hCode(hCode)
                .hMessage(hMessage)
                .build();
    }

    /**
     * 모든 사람 글 보여주기
     *
     * @author hyejinzz, hyunho
     * @since 2021/05/19
     **/
    public BookReviewResponse readRatingReview(BookSearchRequest request) {
        log.info("readRatingReview");
        String hMessage = null;
        StatusEnum hCode = null;
        List<BookReview> bookReviewList = null;
        try {
            LocalDateTime today = LocalDateTime.now();
            bookReviewList = bookReviewRepository.findAllByCreateDateBefore(today, PageRequest.of(request.getStartPage(), 5));
            log.info("bookReviewList : {}", bookReviewList.toString());

            hCode = StatusEnum.hd1004;
            hMessage = "모든 사람 글 가져오기";
        } catch (Exception e) {
            log.error("readRatingReview err :: error msg : {}", e);
            hCode = StatusEnum.hd4444;
            hMessage = "readRatingReview 에러";
        }

        return BookReviewResponse.builder()
                .data(bookReviewList)
                .hCode(hCode)
                .hMessage(hMessage)
                .build();
    }

    /**
     * 글 수정
     *
     * @author hyejinzz
     * @since 2021/05/19
     **/
    @Transactional
    public BookReviewResponse updateReview(BookUpdateRequest request) {
        String hMessage = null;
        StatusEnum hCode = null;

        log.info("updateReview");

        try {
            BookReview bookReview = bookReviewRepository.findByReviewNo(request.getReviewNo());
            bookReview.changeBookReview(request.getRating(), request.getReviewContents());

            hCode = StatusEnum.hd1004;
            hMessage = "글 수정완료";

        } catch (Exception e) {
            log.error("updateReview err :: error msg : {}", e);
            hCode = StatusEnum.hd4444;
            hMessage = "updateReview 에러";
        }

        return BookReviewResponse.builder()
                .hCode(hCode)
                .hMessage(hMessage)
                .build();
    }

    /**
     * 글 삭제
     *
     * @author hyejinzz, hyunho
     * @since 2021/05/30
     **/
    @Transactional
    public BookReviewResponse deleteReview(long reviewNo) {
        String hMessage = null;
        StatusEnum hCode = null;
        try {
            bookReviewRepository.deleteBookReviewByReviewNo(reviewNo);
//            data = resultCode;
            hCode = StatusEnum.hd1004;
            hMessage = "삭제 완료";
        } catch (Exception e) {
            log.error("addBoard err :: error msg : {}", e);
            hCode = StatusEnum.hd4444;
            hMessage = "삭제중 오류";
        }
        return BookReviewResponse.builder()
                .hCode(hCode)
                .hMessage(hMessage)
                .build();
    }

    public BookReviewResponse monthReview(MonthBookReviewRequest monthBookReviewRequest) {
        log.info("monthReview()");
        String hMessage = null;
        StatusEnum hCode = null;
        List<BookReviewWithIsbnDto> result = null;
        try {
            List<BookReview> page = bookReviewRepository.findByBookBetweenDate(
                    monthBookReviewRequest.getUserNo(),
                    monthBookReviewRequest.getStartDate(),
                    monthBookReviewRequest.getEndDate());
            result = page.stream()
                    .map(bookReview -> new BookReviewWithIsbnDto(bookReview))
                    .collect(Collectors.toList());
            log.info("BookReview list : {}", page);

            hCode = StatusEnum.hd1004;
            hMessage = "가져오기";

        } catch (Exception e) {
            log.error("monthReview err :: error msg : {}", e);
            hCode = StatusEnum.hd4444;
            hMessage = "monthReview 에러";
        }

        return BookReviewResponse.builder()
                .data(result)
                .hCode(hCode)
                .hMessage(hMessage)
                .build();
    }

    /**
     * 내 글 보여주기
     *
     * @author hyejinzz
     * @since 2021-05-29
     **/
    public BookReviewResponse bookReviewList(String isbn, int pageNum) {
        log.info(":: readMyReview() :: isbn is {}", isbn);
        String hMessage = "";
        StatusEnum hCode = null;
        List<SearchBookReviewDto> reviewList = null;
        try {
            Page<BookReview> page = bookReviewRepository.findByBookReview(isbn, PageRequest.of(pageNum, 10));
            reviewList = page.getContent().stream()
                    .map(bookReview -> new SearchBookReviewDto(bookReview))
                    .collect(Collectors.toList());
            log.info("readMyReview :: count is {}", page.getTotalElements());
            hCode = StatusEnum.hd1004;
            hMessage = "도서 리뷰 리스트";
        } catch (Exception e) {
            log.error("createReview err :: error msg : {}", e);
            hCode = StatusEnum.hd4444;
            hMessage = "리뷰 리스트 에러";
        }
        return BookReviewResponse.builder()
                .data(reviewList)
                .hCode(hCode)
                .hMessage(hMessage)
                .build();
    }


}
