package com.occi.org.annotations;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @description:
 * @author: occi
 * @date: 2024/10/4
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RemoteService {
    String value() default "";
}
