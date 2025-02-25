package org.dhicc.parkingserviceonboarding.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingAspect {

    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final LoggingRepository loggingRepository;

    @Around("execution(* org.dhicc.parkingserviceonboarding.controller..*(..))")
    public Object logApiRequestResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        int statusCode = 500;

        try {
            logRequest(joinPoint);
            result = joinPoint.proceed();
            statusCode = response.getStatus();
            return result;
        } finally {
            long elapsedTime = System.currentTimeMillis() - startTime;
            logResponse(joinPoint, result, statusCode, elapsedTime);
        }
    }

    private void logRequest(ProceedingJoinPoint joinPoint) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String headers = objectMapper.writeValueAsString(getHeaders(request));
            Object[] args = joinPoint.getArgs();
            String requestBody = objectMapper.writeValueAsString(args);

            log.info("[API 요청] {} {} | Headers: {} | Body: {}", method, uri, headers, requestBody);
        } catch (Exception e) {
            log.error("[API 요청 로깅 오류]", e);
        }
    }

    private void logResponse(ProceedingJoinPoint joinPoint, Object result, int statusCode, long elapsedTime) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String responseBody = objectMapper.writeValueAsString(result);

            log.info("[API 응답] {} {} | Status: {} | Response: {} | 처리 시간: {}ms",
                    method, uri, statusCode, responseBody, elapsedTime);

            // Elasticsearch에 저장
            LoggingDocument logEntry = LoggingDocument.builder()
                    .id(UUID.randomUUID().toString())
                    .method(method)
                    .uri(uri)
                    .headers(getHeaders(request))
                    .requestBody(objectMapper.writeValueAsString(joinPoint.getArgs()))
                    .statusCode(statusCode)
                    .responseBody(responseBody)
                    .elapsedTime(elapsedTime)
                    .timestamp(Instant.now())
                    .build();

            loggingRepository.save(logEntry);

        } catch (Exception e) {
            log.error("[API 응답 로깅 오류]", e);
        }
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        return headers;
    }

    @AfterThrowing(pointcut = "execution(* org.dhicc.parkingserviceonboarding.controller..*(..))", throwing = "ex")
    public void logException(Exception ex) {
        log.error("[API 오류 발생] {}: {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
    }
}
