package com.occi.org.user;

import com.occi.org.netty.model.Response;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: occi
 * @date: 2024/10/1
 */
@Service
public class UserService {

    public void saveUser(User user) {
        Response response = new Response("00000", "保存用户信息成功");
        System.out.println("保存用户信息成功"+user.toString()); // 实际上是数据库的工作
        return;
    }
}
