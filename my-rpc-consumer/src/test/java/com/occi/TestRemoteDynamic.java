package com.occi;

import com.alibaba.fastjson.JSONObject;
import com.occi.org.client.annotation.RemoteInvoke;
import com.occi.org.client.param.Response;
import com.occi.org.user.User;
import com.occi.org.user.UserRemote;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @description:
 * @author: occi
 * @date: 2024/10/4
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=TestRemoteDynamic.class)
@ComponentScan("com.occi")
public class TestRemoteDynamic {
    
    @RemoteInvoke
    private UserRemote userRemote;


    @Test
    public void testSaveUser(){
        // 测试执行时间
        // 保存10000个用户
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            Response response = userRemote.saveUser(new User(100011L, "张三", "123456", "2", "2"));
            System.out.println(JSONObject.toJSONString(response));
        }
        // 测试10万次耗时17秒
        System.out.println("耗时秒数：" + (System.currentTimeMillis() - start) / 1000);
    }
}
