package com.semobook.intercepter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.semobook.common.SemoConstant.SEMO_BOOK_API_AUTHORIZATION;

@Slf4j
public class Interceptor extends HandlerInterceptorAdapter {

    //컨트롤러에 도착하기전에 동작하는 메소드로 return값이 true면 진행, false면 stop
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorization = "";
        authorization = request.getHeader("Authorization");
        log.info("Authorization = {}", authorization);
        if (!authorization.equals(SEMO_BOOK_API_AUTHORIZATION)) {
            return false;
        }
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }
}
