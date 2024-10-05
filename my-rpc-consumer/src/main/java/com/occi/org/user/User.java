package com.occi.org.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: occi
 * @date: 2024/10/1
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class User {

    private Long id;
    private String name;

    private String email;
    private String phone;
    private String address;


}
