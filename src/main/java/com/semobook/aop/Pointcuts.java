package com.semobook.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut("execution(* com.semobook.bookReview..*(..))")
    public void allBookReview(){}

    @Pointcut("execution(* com.semobook.bookwant..*(..))")
    public void allBookWant(){}

    @Pointcut("execution(* com.semobook.book..*(..))")
    public void allBook(){}

    @Pointcut("execution(* com.semobook.user..*(..))")
    public void allUser(){}

    @Pointcut("execution(* com.semobook.notice..*(..))")
    public void allNotice(){}

    @Pointcut("allBookReview() || allBookWant() || allBook() || allUser() || allNotice()")
    public void allLogicMethod(){}


}
