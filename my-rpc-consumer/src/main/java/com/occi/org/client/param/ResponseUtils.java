package com.occi.org.client.param;

/**
 * @description:
 * @author: occi
 * @date: 2024/10/3
 */
public class ResponseUtils {

    public static Response defaultSuccessResponse(Object content) {
        Response response = new Response();
        response.setCode("00000");
        response.setResult(content);
        return response;
    }




}
