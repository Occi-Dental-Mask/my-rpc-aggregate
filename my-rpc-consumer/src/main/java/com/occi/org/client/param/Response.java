package com.occi.org.client.param;

/**
 * @description:
 * @author: occi
 * @date: 2024/10/1
 */
public class Response {

    private Long id;
    private Object result;

    private String code; //00000表示成功，其他表示失败

    private String msg;//失败信息

    public Response() {
    }


    public Response(Long id, Object result) {
        this.id = id;
        this.result = result;
    }
    public Response(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public Response(Long id, Object result, String code, String msg) {
        this.id = id;
        this.result = result;
        this.code = code;
        this.msg = msg;
    }

    public Long getId() {
        return id;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
