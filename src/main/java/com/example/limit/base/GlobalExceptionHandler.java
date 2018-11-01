package com.example.limit.base;

import com.example.limit.access.exception.AccessLimitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessLimitException.class)
    public CommonResult handleIllegalParamException(Throwable ex) {
        return CommonResult.fail(ex.getLocalizedMessage());
    }
}
