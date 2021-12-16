package com.semobook.intercepter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.semobook.common.SemoConstant.SEMO_BOOK_API_AUTHORIZATION;

@Slf4j
public class Interceptor implements HandlerInterceptor {
    //컨트롤러에 도착하기전에 동작하는 메소드로 return값이 true면 진행, false면 stop
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorization = request.getHeader("Authorization");
        if (authorization == null) {
            log.info("Authorization is null");
            return false;
        }
        if (!authorization.equals(SEMO_BOOK_API_AUTHORIZATION)) {
            log.info("Authorization fail = {}", authorization);
            return false;
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
