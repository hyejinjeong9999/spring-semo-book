package com.semobook.book.repository;

import com.semobook.book.domain.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface BookRepository extends CrudRepository<Book, String>, BookRepositoryCustom{

    //ISBN(PK) 으로 도서 종보 조회
//    Book findByIsbn(@Param("isbn") String isbn);

    //ISBN(PK) 으로 도서, 도서에 포함된 리뷰 정보 조회
//    @Query(value = "select b from Book b " +
//            "join fetch b.bookReviewList br " +
//            "join fetch br.userInfo where b.isbn = :isbn")
//    Book findByIsbnWithReview(@Param("isbn")String isbn);

    //책 전채 조회(패이징처리)//
    //page
//    @Query(value = "select b from Book b left join fetch b.bookReviewList br",
//            countQuery = "select count(b.bookName) from Book b")
//    Page<Book> findAll(Pageable pageable);
    //slice
//    @Query(value = "select b from Book b left join fetch b.bookReviewList br")
//    Slice<Book> findAll(Pageable pageable);

    //책 추천시 카테고리 조회
    List<Book> findAllByCategory(String category);
    //책 추천시 키워드고리 조회
    List<Book> findAllByKeyword(String category);

    //책 저장
//    Book save(Book book);

    //책 제거
//    long deleteBookByIsbn (String isbn);

    //도서 존재 여부
//    boolean existsByIsbn(String isbn);


}