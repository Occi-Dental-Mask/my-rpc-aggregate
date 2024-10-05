package com.occi.org.client.constants;

/**
 * @description:
 * @author: occi
 * @date: 2024/10/5
 */
public enum Constants {

    // 服务路径
    SERVER_REGISTER_PATH("/netty"),
    ;
    private String value;
    Constants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
