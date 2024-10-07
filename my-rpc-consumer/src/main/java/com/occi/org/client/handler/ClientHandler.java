package com.occi.org.client.handler;

import com.alibaba.fastjson.JSONObject;
import com.occi.commons.Response;
import com.occi.org.client.core.ResultFuture;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @description:
 * @author: occi
 * @date: 2024/10/1
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg.toString().equals("ping")) {
            System.out.println("收到服务端的读空闲ping,向服务端发送pong");
            ctx.channel().writeAndFlush("pong\r\n");
            return;
        } else if (msg.toString().equals("pong")) {
            System.out.println("收到服务端的pong消息，确认连接正常");
            return;
        }
        //设置response
        Response response = JSONObject.parseObject(msg.toString(), Response.class);
        ResultFuture.receive(response);//通过response的ID可以在map中找到对应的Request,并为相应的request设置response,使得调用get()客户端得到结果
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                System.out.println("客户端写空闲，发送心跳");
                ctx.writeAndFlush("ping\r\n");
            } else if (event.state() == IdleState.ALL_IDLE) {
                System.out.println("客户端读写空闲，断开连接");
                // 断开连接
                ctx.channel().close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
