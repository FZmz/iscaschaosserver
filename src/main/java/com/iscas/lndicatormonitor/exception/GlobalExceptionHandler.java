package com.iscas.lndicatormonitor.exception;

import com.iscas.lndicatormonitor.common.CustomResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.apache.catalina.connector.ClientAbortException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 添加安全响应头到响应中
    private void addSecurityHeaders(HttpServletResponse response) {
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Content-Security-Policy", 
                "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                "style-src 'self' 'unsafe-inline'; img-src 'self' data:; " +
                "font-src 'self'; connect-src 'self' *; frame-src 'self'");
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        response.setHeader("X-Permitted-Cross-Domain-Policies", "none");
        response.setHeader("X-Download-Options", "noopen");
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<CustomResult> handleException(Exception e, HttpServletResponse response) {
        log.error("系统异常: ", e);
        addSecurityHeaders(response);
        return new ResponseEntity<>(new CustomResult(50000, "服务器错误", null), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    public ResponseEntity<CustomResult> handleUnauthorizedException(UnauthorizedException e, HttpServletResponse response) {
        log.warn("未授权访问: {}", e.getMessage());
        addSecurityHeaders(response);
        return new ResponseEntity<>(new CustomResult(40100, e.getMessage(), null), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseBody
    public ResponseEntity<CustomResult> handleForbiddenException(ForbiddenException e, HttpServletResponse response) {
        log.warn("权限不足: {}", e.getMessage());
        addSecurityHeaders(response);
        return new ResponseEntity<>(new CustomResult(40000, "无权限访问", null), HttpStatus.FORBIDDEN);
    }
    
    /**
     * 处理文件上传大小超过限制异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public ResponseEntity<CustomResult> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e, HttpServletResponse response) {
        log.error("文件上传大小超过限制: {}", e.getMessage());
        addSecurityHeaders(response);
        return new ResponseEntity<>(new CustomResult(40000, "上传文件过大，最大支持10MB", null), HttpStatus.PAYLOAD_TOO_LARGE);
    }
    
    /**
     * 处理请求内容过大的异常
     */
    @ExceptionHandler({ClientAbortException.class, IOException.class, ServletException.class})
    @ResponseBody
    public ResponseEntity<CustomResult> handleContentTooLargeException(Exception e, HttpServletResponse response) {
        // 仅处理与请求内容过大相关的异常
        addSecurityHeaders(response);
        if (e.getMessage() != null && (
                e.getMessage().contains("too large") || 
                e.getMessage().contains("exceed") || 
                e.getMessage().contains("size") ||
                e.getMessage().contains("Content-Length"))) {
            log.error("请求数据过大: {}", e.getMessage());
            return new ResponseEntity<>(new CustomResult(40000, "请求数据过大，最大支持10MB", null), HttpStatus.PAYLOAD_TOO_LARGE);
        }
        // 其他类型的这些异常，交给通用异常处理器处理
        return handleException(e, response);
    }
} 