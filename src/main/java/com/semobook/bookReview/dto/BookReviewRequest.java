package com.semobook.bookReview.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookReviewRequest {
    @Schema(description = "유저no" , example = "12")
    long userNo;
    @Schema(description = "책isbn" , example = "9788998139766")
    String isbn;
    @Schema(description = "평점" , example = "3")
    int rating;
    @Schema(description = "리뷰내용" , example = "정말재밌는책이었다.")
    String reviewContents;
    @Schema(description = "시작페이지, 시작페이지 기준으로 5개 나옴" , example = "0")
    int startPage;
}
