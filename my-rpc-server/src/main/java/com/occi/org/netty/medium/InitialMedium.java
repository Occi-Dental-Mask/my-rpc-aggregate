package com.occi.org.netty.medium;

import com.occi.org.annotations.Remote;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * @description:
 * @author: occi
 * @date: 2024/10/1
 */
@Component
public class InitialMedium implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (bean.getClass().isAnnotationPresent(Remote.class)) {
            Method[] declaredMethods = bean.getClass().getDeclaredMethods();
            for (Method method : declaredMethods) {
                String key = bean.getClass().getInterfaces()[0].getName() + "." + method.getName();
                HashMap<String, BeanMethod> map = (HashMap<String, BeanMethod>) Medium.beanMethodMap;
                BeanMethod beanMethod = new BeanMethod();
                beanMethod.setBean(bean);
                beanMethod.setMethod(method);
                map.put(key,beanMethod);
            }
        }
        return bean;
    }
}
