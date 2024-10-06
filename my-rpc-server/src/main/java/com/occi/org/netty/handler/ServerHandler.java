package com.occi.org.netty.handler;

import com.alibaba.fastjson.JSONObject;
import com.occi.commons.Response;
import com.occi.org.netty.medium.Medium;
import com.occi.org.netty.model.ServerRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description:
 * @author: occi
 * @date: 2024/9/30
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg.toString().equals("pong")) {
            System.out.println("收到客户端Pong消息，确认连接正常");
            // 交给下一个handler处理
            return;
        }
        // msg:{"command":"com.occi.org.user.UserRemote.saveUser","content":{"address":"2","email":"123456","id":100011,"name":"张三","phone":"2"},"id":1}
        ServerRequest request = JSONObject.parseObject(msg.toString(), ServerRequest.class);
        System.out.println(request.getCommand());
        Medium medium = Medium.newInstance();
        Response response = medium.process(request);
        ctx.channel().writeAndFlush(JSONObject.toJSONString(response) + "\r\n");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                System.out.println("读空闲");
//                ctx.channel().close();
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                System.out.println("写空闲");
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                System.out.println("读写空闲");
                ctx.channel().writeAndFlush("ping\r\n");
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("server caught exception", cause);
        ctx.close();
    }
}
