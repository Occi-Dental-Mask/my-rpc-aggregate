package com.occi.org.client.param;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @description:
 * @author: occi
 * @date: 2024/10/1
 */
public class ClientRequest {

    private final long id;
    private Object content;
    private static AtomicLong realID = new AtomicLong(0);

    private String command;


    public ClientRequest() {
        this.id = realID.incrementAndGet();
    }


    public Long getId() {
        return id;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public void setCommand(String command) {
        this.command = command;
    }


    public Object getContent() {
        return content;
    }

    public String getCommand() {
        return command;
    }
}
