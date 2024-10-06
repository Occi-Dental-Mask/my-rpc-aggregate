package com.occi.org.netty.medium;

import java.lang.reflect.Method;

/**
 * @description:
 * @author: occi
 * @date: 2024/10/1
 */
public class BeanMethod {

    private Object bean;
    private Method method;

    BeanMethod(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }

    public Object getBean() {
        return bean;
    }

    public Method getMethod() {
        return method;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
