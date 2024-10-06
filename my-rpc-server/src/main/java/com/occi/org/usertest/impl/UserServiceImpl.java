package com.occi.org.usertest.impl;

import com.occi.commons.Response;
import com.occi.commons.ResponseUtils;
import com.occi.user.UserService;
import com.occi.org.annotations.RemoteService;

/**
 * @description:
 * @author: occi
 * @date: 2024/10/3
 */
@RemoteService
public class UserServiceImpl implements UserService {

    @Override
    public Response saveUser(com.occi.user.User user) {
        Response response = ResponseUtils.defaultSuccessResponse("保存用户信息成功");
        System.out.println("保存用户信息成功"+user.toString()); // 实际上是数据库的工作
        return response;
    }

    @Override
    public void deleteUser(Long id) {
        System.out.println("删除用户信息成功"+id); // 实际上是数据库的工作
    }
}
