package com.elk.config;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
public class LogInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String tracingId = request.getHeader("eventTraceId");
        if (!StringUtils.hasLength(tracingId)) {
            tracingId = UUID.randomUUID().toString();
        }
        MDC.put("eventTraceId", tracingId);

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        MDC.remove("eventTraceId");
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}