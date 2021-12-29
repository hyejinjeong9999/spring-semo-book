package com.semobook.book.controller;

import com.semobook.book.dto.BookRequest;
import com.semobook.book.dto.BookResponse;
import com.semobook.book.dto.BookSearchRequest;
import com.semobook.book.service.BookService;
import com.semobook.common.response.ListResponse;
import com.semobook.common.response.SingleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Tag(name = "BookController")
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;


    /**
     * 책 등록
     *
     * @author khh
     * @since 2021/04/25 +
     **/
    @Operation(description = "책 등록")
    @PostMapping("/books/new")
    public ResponseEntity<SingleResponse> addBookCon(@Parameter @RequestBody BookRequest bookRequest) {
        log.info("/signup :: userId : {} :: userPw : {} :: userName : {} ===", bookRequest.getIsbn(), bookRequest.getBookName(), bookRequest.getAuthor());
        return ResponseEntity.ok(bookService.addBook(bookRequest));
    }

    /**
     * delete book
     *
     * @author hyunho
     * @since 2021/05/27
     **/
    @Operation(description = "도서 삭제")
    @DeleteMapping("/books/{isbn}")
    public ResponseEntity<SingleResponse> deleteBookCon(@Parameter @PathVariable String isbn) {
        log.info(":: deleteBookCon  :: isbn is {}", isbn);
        return ResponseEntity.ok(bookService.deleteBook(isbn));
    }


    @Operation(description = "도서 검색(검색어)")
    @GetMapping("/books/list")
    public ResponseEntity<BookResponse> searchBookCon(@Parameter BookSearchRequest bookSearchRequest){
        log.info("/search :: keyword : {} :: pageNum : {} ===", bookSearchRequest.getKeyword(), bookSearchRequest.getPageNum());
        return ResponseEntity.ok(bookService.searchBook(bookSearchRequest));
    }

    /**
     * 책 검색 (ISBN)
     *
     * @author khh
     * @since 2021/04/25
     **/
    @Operation(description = "책 조회")
    @GetMapping(value = "/books/{isbn}")
    public ResponseEntity<SingleResponse> findBookCon(@Parameter @PathVariable String isbn) {
        log.info("==/findBook {}", isbn);
        return ResponseEntity.ok(bookService.findBook(isbn));
    }


    /**
     * 책 조회
     *
     * @author hyunho
     * @since 2021/06/05
     **/
    @Operation(description = "책 조회(리뷰 포함)")
    @GetMapping(value = "/books/review/{isbn}")
    public ResponseEntity<SingleResponse> findBookWithReviewCon(@Parameter @PathVariable String isbn) {
        log.info("==/findBookWithReview {}", isbn);
        return ResponseEntity.ok(bookService.findBookWithReview(isbn));
    }

    /**
     * 책 검색(ALL)
     *
     * @author khh
     * @since 2021/04/25
     **/
    @Operation(description = "모든 책 조회")
    @GetMapping("/books/all/{page}")
    public ResponseEntity<ListResponse> findAllCon(@Parameter @PathVariable int page) {
        log.info("==/findAll");
        return ResponseEntity.ok(bookService.findAll(page));
    }

//    @Operation(description = "모든 책 조회")
//    @GetMapping("/v1/page/books")
//    public ResponseEntity<BookResponse> pageFindAll(){
//        log.info("==/findAll");
//        return ResponseEntity.ok(bookService.pageBookList());
//    }


    /**
     * 리뷰가 포함된 책 검색(All)
     *
     * @author hyunho
     * @since 2021/06/02
     **/
//    @Operation(description = "리뷰가 포함된 책 조회")
//    @GetMapping("/bookWithReviews")
//    public ResponseEntity<BookResponse> findAllWithReview(){
//        log.info("==/findAllWithReview");
//        return ResponseEntity.ok(bookService.findAllWithReview());
//    }
}