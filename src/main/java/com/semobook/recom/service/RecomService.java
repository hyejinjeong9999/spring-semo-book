package com.semobook.recom.service;

import com.semobook.bookReview.dto.BookReviewRequest;
import com.semobook.book.domain.Book;
import com.semobook.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class RecomService {

    //1순위 : 유저가 리뷰를 쓰면 -> 리뷰책과 관련된 장르의 책 추천 REDIS KEY : USER_REVIEW_RECOM
            //1. 이책을 본 사람들의 다른 책들 추천
            //2. 이책과 비슷한 장르 추천
    //2순위 : 유저가 종합적으로 준 평점 기반 추천 : USER_TOTAL_RECOM
    //3순위 : 유저 정보 (나이, 성별) 기반 추천 : USER_INFO_RECOM
    //4순위 : 베스트 셀러 추천 : BSETSELLER_RECOM

    private final BookRepository bookRepository;



    /**
     *
     * admin recomnned
     * @author hjjung
     * @since 2021-05-21
     **/

    /**
     *
     * user recom
     * @author hjjung
     * @since 2021-05-21
     **/




    /**
     * 책과 관련된 장르의 책을 추천
     * @param boardRequest
     */
    public void updateUserReviewRecom(String isbn) {
        /**
        * Book <-> BookReview 연관관계 변경하면서 ISBN값(FK) 가져오는 부분 수정해야해서 주석처리 하였습니다.
        *
        * @author hyunho
        * @since 2021/05/23
        **/
//        //내가 읽은 책의 isbn
//        String isbn = boardRequest.getBookReview().getIsbn();
//        //책의 카테고리 가져온다.
//        Book book = bookRepository.findByIsbn(isbn);
//        String category = book.getCategory();
//
//        if (category != null) {
//            //카테고리 같은 책들 가져오기
//            List<Book> recomBookList = bookRepository.findAllByCategory(category);
//        }
    }



        /**
         *
         * 추천하는 책에서 이용자가 원하지 않는 데이터들을 제거함
         * @author hjjung
         * @since 2021-05-16
         **/
    private List<Book> recomfilter(List<Book> recomList){

        return null;
    }
    

}
