package com.occi.org.user;

import com.occi.org.netty.model.Response;

/**
 * @description:
 * @author: occi
 * @date: 2024/10/3
 */
public interface UserRemote {

    public Response saveUser(User user);

}
