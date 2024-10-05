package com.occi.org.constants;

/**
 * @description:
 * @author: occi
 * @date: 2024/10/5
 */
public enum Constants {
    ZK_NODE_PATH("/netty"),
    ;
    private String value;
    Constants(String s) {
        this.value = s;
    }

    public String getValue() {
        return value;
    }
}
