package com.snackstore.common;

import javax.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(BizException.class)
  public ApiResponse<Void> handleBiz(BizException ex) {
    return ApiResponse.error(ex.getCode(), ex.getMessage());
  }

  @ExceptionHandler({
      MethodArgumentNotValidException.class,
      BindException.class,
      ConstraintViolationException.class,
      HttpMessageNotReadableException.class
  })
  public ApiResponse<Void> handleBadRequest(Exception ex) {
    return ApiResponse.error(400, "请求参数错误");
  }

  @ExceptionHandler(Exception.class)
  public ApiResponse<Void> handleOther(Exception ex) {
    return ApiResponse.error(500, "服务器错误");
  }
}

