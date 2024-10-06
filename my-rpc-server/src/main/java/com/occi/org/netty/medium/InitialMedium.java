package com.occi.org.netty.medium;

import com.occi.org.annotations.RemoteService;
import com.occi.org.factory.ZookeeperFactory;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Method;
import java.net.InetAddress;
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

        if (bean.getClass().isAnnotationPresent(RemoteService.class)) {
            Method[] declaredMethods = bean.getClass().getDeclaredMethods();
            // 获取接口全名
            String serviceName = bean.getClass().getInterfaces()[0].getName();

            for (Method method : declaredMethods) {
                String key = serviceName + "." + method.getName();
                BeanMethod beanMethod = new BeanMethod(bean, method);
                Medium.beanMethodMap.put(key,beanMethod);
                // 注册接口名称到zookeeper
//                registerServiceToZookeeper(serviceName);
            }
        }
        return bean;
    }

    private void registerServiceToZookeeper(String serviceName) {
        try {
            CuratorFramework client = ZookeeperFactory.getClient();
            if (client != null) {
                String servicePath = "/netty/" + serviceName;
                String serviceInfo = InetAddress.getLocalHost().getHostAddress() + ":7999";

                // 如果服务节点不存在则创建
                if (client.checkExists().forPath(servicePath) == null) {
                    client.create().creatingParentsIfNeeded().forPath(servicePath, serviceInfo.getBytes());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
