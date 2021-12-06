package com.semobook.config;

import com.semobook.aop.trace.LogTrace;
import com.semobook.aop.trace.LogTraceAspect;
import com.semobook.aop.trace.ThreadLocalLogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogTraceConfig {
    @Bean
    public LogTraceAspect logTraceAspect() {
        return new LogTraceAspect(logTrace());
    }
    @Bean
    public LogTrace logTrace() {
        return new ThreadLocalLogTrace();
    }
}
