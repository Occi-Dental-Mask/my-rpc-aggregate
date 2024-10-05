package com.occi.org.user.impl;

import com.occi.org.annotations.Remote;
import com.occi.org.netty.model.Response;
import com.occi.org.netty.model.ResponseUtils;
import com.occi.org.user.User;
import com.occi.org.user.UserRemote;
import com.occi.org.user.UserService;
import javax.annotation.Resource;

/**
 * @description:
 * @author: occi
 * @date: 2024/10/3
 */
@Remote
public class UserRemoteImpl implements UserRemote {

    @Resource
    private UserService service;
    @Override
    public Response saveUser(User user) {
        service.saveUser(user);
        return ResponseUtils.defaultSuccessResponse(user);
    }
}
