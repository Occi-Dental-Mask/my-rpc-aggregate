package com.occi.commons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: occi
 * @date: 2024/10/6
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private Long id;
    private Object result;

    private String code; //00000表示成功，其他表示失败

    private String msg;//失败信息

    public Response(Long id, Object result) {
        this.id = id;
        this.result = result;
    }
    public Response(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
