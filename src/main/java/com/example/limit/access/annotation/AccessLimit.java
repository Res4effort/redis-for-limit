package com.example.limit.access.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimit {

    /**
     * SpEL for generating key
     */
    String SpEL() default "";

    /**
     * Access times
     */
    int times() default -1;

    /**
     * time of duration for compute access times
     */
    int duration() default 60 * 60 * 24;

    /**
     * tip message when user is limited
     */
    String tipMsg() default "Too fast, try again later！";
}
