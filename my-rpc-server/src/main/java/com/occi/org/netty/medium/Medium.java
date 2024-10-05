package com.occi.org.netty.medium;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.occi.org.netty.model.Response;
import com.occi.org.netty.model.ServerRequest;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: occi
 * @date: 2024/10/1
 */
public class Medium {

    private static final Medium media = null;

    public static Map<String, BeanMethod> beanMethodMap = new HashMap<>();


    public static Medium newInstance() {
        return media == null ? new Medium() : media;
    }

    public Response process(ServerRequest request) {
        Response response = null;
        try {
            String command = request.getCommand();
            BeanMethod beanMethod = beanMethodMap.get(command);

            if (beanMethod == null) {
                return null;
            }
            Object bean = beanMethod.getBean();
            Method method = beanMethod.getMethod();
            Class<?>[] parameterTypes = method.getParameterTypes();
            JSONObject jsonObject = JSON.parseObject(JSONObject.toJSONString(request.getContent()));


            Object[] args = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                args[i] = jsonObject.toJavaObject(parameterTypes[i]);
            }
            // 任何一个方法必须返回Response对象，否则会报错
            response = (Response) method.invoke(bean, args);
            response.setId(request.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
