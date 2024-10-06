package com.occi.user;

import com.occi.commons.Response;
/**
 * @description:
 * @author: occi
 * @date: 2024/10/6
 */
public interface UserService {
    public Response saveUser(User user);

    public void deleteUser(Long id);
}
