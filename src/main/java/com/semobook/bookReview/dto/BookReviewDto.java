package com.semobook.bookReview.dto;

import com.semobook.bookReview.domain.BookReview;
import com.semobook.bookReview.domain.ReviewStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookReviewDto {

    private long reviewNo;
    private int rating;
    private String reviewContents;
    private LocalDateTime createDate;
    private int declaration;
    private ReviewStatus reviewStatus;

    public BookReviewDto(BookReview bookReview) {
        reviewNo = bookReview.getReviewNo();
        rating = bookReview.getRating();
        reviewContents = bookReview.getReviewContents();
        createDate = bookReview.getCreateDate();
        declaration = bookReview.getDeclaration();
        reviewStatus = bookReview.getReviewStatus();
    }
}
