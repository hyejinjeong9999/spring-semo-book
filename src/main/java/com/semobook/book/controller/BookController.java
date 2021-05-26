package com.semobook.book.controller;

import com.semobook.book.domain.Book;
import com.semobook.book.dto.BookDeleteRequest;
import com.semobook.book.dto.BookRequest;
import com.semobook.book.dto.BookResponse;
import com.semobook.book.service.BookService;
import com.semobook.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "BookController")
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;


    /**
     * 책 등록
     *
     * @author khh
     * @since 2021/04/25
    **/
    @Operation(description = "책 등록")
    @PostMapping("/addBook")
    public ResponseEntity<BookResponse> addBook(@Parameter @RequestBody BookRequest bookRequest){
        log.info("/signup :: userId : {} :: userPw : {} :: userName : {} ===", bookRequest.getIsbn(), bookRequest.getBookName(), bookRequest.getAuthor());
        return ResponseEntity.ok(bookService.addBook(bookRequest));
    }


    @Operation(description = "도서 삭제")
    @PostMapping("/delete")
    public ResponseEntity<BookResponse> deleteBookCon(@Parameter @RequestParam String isbn){
        log.info(":: deleteBookCon  :: isbn is {}", isbn);
        return ResponseEntity.ok(bookService.deleteBook(isbn));
    }

    /**
     * 책 검색
     *
     * @author khh
     * @since 2021/04/25
    **/
    @Operation(description = "책 조회")
    @GetMapping(value = "/book/{isbn}")
    public ResponseEntity<BookResponse> findBook(@Parameter @PathVariable String isbn){
        log.info("==/findBook {}", isbn);
        return ResponseEntity.ok(bookService.findBook(isbn));
    }

    /**
     * 책 검색(ALL)
     *
     * @author khh
     * @since 2021/04/25
    **/
    @Operation(description = "모든 책 조회")
    @GetMapping("/books")
    public ResponseEntity<BookResponse> findAll(){
        log.info("==/findAll");
        //TODO:패이지 처리
        return ResponseEntity.ok(bookService.findAll());
    }



}