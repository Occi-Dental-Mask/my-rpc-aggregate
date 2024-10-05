package com.occi.org.client.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @description:
 * @author: occi
 * @date: 2024/10/4
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RemoteInvoke {
}
