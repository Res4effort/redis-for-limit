package com.example.limit.base;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommonResult {

    private int code;
    private String msg;
    private Object data;

    public static CommonResult success(Object data) {
        return CommonResult.builder().code(1).msg("success").data(data).build();
    }

    public static CommonResult fail(Object data) {
        return CommonResult.builder().code(1).msg("fail").data(data).build();
    }
}
