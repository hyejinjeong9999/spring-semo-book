package com.semobook.aop.performance;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class PerformanceAspect {

//    @Around("execution(* com.semobook..*.UserController.*(..))")  //포인트컷 표현식을 이용한 방법
    @Around("@annotation(performanceCheck)") //annotation을 이용한 방법
    public Object calculatePerformanceTime(ProceedingJoinPoint joinPoint, PerformanceCheck performanceCheck)  throws Throwable{
        try {
            long start = System.currentTimeMillis();
            Object result = joinPoint.proceed();
            long end = System.currentTimeMillis();
            log.info("[PerformanceTime] 수행 시간 = {} ms", end - start);
            return result;
        } catch (Exception e) {
            log.info("[PerformanceTime] Exception = {}",joinPoint.getSignature());
            throw e;
        }finally {
            log.info("[PerformanceTime] {}",joinPoint.getSignature());
        }
    }
}
