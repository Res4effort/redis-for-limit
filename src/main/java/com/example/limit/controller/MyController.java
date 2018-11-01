package com.example.limit.controller;

import com.example.limit.access.annotation.AccessLimit;
import com.example.limit.access.util.AccessLimitUtil;
import com.example.limit.base.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/v1")
public class MyController {

    @AccessLimit(SpEL = "#getUniqueId(#request)", times = 5, duration = 60, tipMsg = "sorry, try 1m later!")
    @RequestMapping(value = "/sayHello", method = RequestMethod.GET)
    public CommonResult sayHello(HttpServletRequest request) {
        log.info("Hello ip:" + AccessLimitUtil.getUniqueId(request));
        return CommonResult.success("hello");
    }

    @AccessLimit(SpEL = "#getUniqueId(#request)", times = 50, duration = 60)
    @RequestMapping(value = "/sayBye", method = RequestMethod.GET)
    public CommonResult sayBye(HttpServletRequest request, @RequestParam("msg") String msg) {
        log.info("Bye ip:" + AccessLimitUtil.getUniqueId(request) + "\n" + "msg:" + msg);
        return CommonResult.success("Bye");
    }
}
