package com.semobook.boardReview.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Entity
public class BookReview {
    @Id
    @GeneratedValue
    private long reviewNo;
    private String isbn;
    private String userNo;
    private int rating;
    private String reviewContents;
    private LocalDateTime createDate;
    private int declaration;

    @Builder
    public BookReview(long reviewNo, String isbn, String userNo, int rating,
                      String reviewContents, LocalDateTime createDate, int declaration){
     this.reviewNo = reviewNo;
     this.isbn = isbn;
     this.userNo = userNo;
     this.rating = rating;
     this.reviewContents = reviewContents;
     this.createDate = createDate;
     this.declaration = declaration;

    }

}
