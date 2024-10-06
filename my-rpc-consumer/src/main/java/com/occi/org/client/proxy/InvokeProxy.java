package com.occi.org.client.proxy;

import com.occi.org.client.annotation.RemoteInvoke;
import com.occi.org.client.core.ResultFuture;
import com.occi.org.client.core.TCPClient;
import com.occi.org.client.param.ClientRequest;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: occi
 * @date: 2024/10/4
 */
@Component
public class InvokeProxy implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();

        for (Field field : fields) {
            // 如果属性上有RemoteInvoke注解,则进行代理
            if (field.isAnnotationPresent(RemoteInvoke.class)) {
                final Map<Method, Class> methodMap = new HashMap<>();
                putMethodClass(methodMap, field);
                field.setAccessible(true);
                // 代理对象
                Enhancer enhancer = new Enhancer();
                enhancer.setInterfaces(new Class[]{field.getType()});
                enhancer.setCallback(new MethodInterceptor() {
                    @Override
                    public Object intercept(Object instance, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                        // 拦截，会执行代理对象这里的代码
                        ClientRequest clientRequest = new ClientRequest();
                        clientRequest.setContent(args[0]);
                        String command = methodMap.get(method).getName() + "." + method.getName();
                        clientRequest.setCommand(command);
                        // 异步发送请求
                        ResultFuture resultFuture = TCPClient.send(clientRequest);
                        // 设置超时时间
                        Long timeOut = 60L;
                        // 阻塞,超时等待结果
                        if (resultFuture == null) {
                            return null;
                        }
                        return resultFuture.get(timeOut);
                    }
                });
                try {
                    field.set(bean, enhancer.create());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return bean;
    }

    /**
     * 将属性的所有方法和属性类型放入到HashMap中
     * @param methodMap
     * @param field
     */
    private void putMethodClass(Map<Method, Class> methodMap, Field field) {
        Method[] methods = field.getType().getDeclaredMethods();
        for (Method method : methods) {
            methodMap.put(method, field.getType());
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
