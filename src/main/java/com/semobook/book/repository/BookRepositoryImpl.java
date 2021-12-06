package com.semobook.book.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.semobook.book.domain.Book;
import com.semobook.aop.performance.PerformanceCheck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static com.semobook.book.domain.QBook.book;
import static com.semobook.bookReview.domain.QBookReview.bookReview;

public class BookRepositoryImpl implements BookRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public BookRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    /**
     * ISBN값으로 도서 검색
     *
     * @author hyunho
     * @since 2021/07/29
    **/
    @Override
    public Book findByIsbn(String isbn) {
        return queryFactory
                .selectFrom(book)
                .where(book.isbn.eq(isbn))
                .fetchOne();
    }

    /**
     * ISBN(PK)으로 도서, 도서에 포함된 리뷰 정보 조회
     *
     * @author hyunho
     * @since 2021/07/29
    **/
    @Override
    public Book findByIsbnWithReview(String isbn) {
        return queryFactory
                .selectFrom(book)
                .join(book.bookReviewList, bookReview).fetchJoin()
                .join(bookReview.userInfo).fetchJoin()
                .where(book.isbn.eq(isbn))
                .fetchOne();
    }

    @Override
    public Page<Book> findAll(Pageable pageable) {
        List<Book> results = queryFactory
                .selectFrom(book)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(book)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<Book> findAllByCategory(Pageable pageable,String category) {
        List<Book> results = queryFactory
                .selectFrom(book)
                .where(categoryEq(category))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(book)
                .where(categoryEq(category))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }


    /**
    * 책 삭제
    * @author hyejinzz
    * @since 2021-08-03
    **/
    @Override
    public long deleteBookByIsbn(String isbn) {
        long fetchOne = queryFactory
                .delete(book)
                .where(isbnEq(isbn))
                .execute();

        return fetchOne;

    }

    /**
    * 책 있는지 조회
    * @author hyejinzz
    * @since 2021-08-03
    **/
    @Override
    @PerformanceCheck
    public boolean existsByIsbn(String isbn) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(book)
                .where(isbnEq(isbn))
                .fetchFirst();
        return fetchOne != null;
    }

    @Override
    @PerformanceCheck
    public long existUsingCount(String isbn) {
        long total = queryFactory
                .select()
                .from(book)
                .where(isbnEq(isbn))
                .fetchCount();
        return total;
    }

    private BooleanExpression isbnEq(String isbn) {
        return isbn != null ? book.isbn.eq(isbn) : null;
    }

    private BooleanExpression categoryEq(String cate) {
        return cate != null ? book.category.eq(cate) : null;
    }


}
