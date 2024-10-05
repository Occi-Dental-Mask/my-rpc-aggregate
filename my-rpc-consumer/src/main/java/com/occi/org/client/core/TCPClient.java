package com.occi.org.client.core;


import com.alibaba.fastjson.JSONObject;
import com.occi.org.client.constants.Constants;
import com.occi.org.client.handler.ClientHandler;
import com.occi.org.client.param.ClientRequest;
import com.occi.org.client.factory.ZookeeperFactory;
import com.occi.org.client.param.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * @description:
 * @author: occi
 * @date: 2024/10/1
 */
public class TCPClient {

    static final Bootstrap b = new Bootstrap();
    static final Logger logger = LoggerFactory.getLogger(TCPClient.class);

    static {

        EventLoopGroup workerGroup = new NioEventLoopGroup();
        b.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(10, 5, 10));
                        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(65535, Delimiters.lineDelimiter()[0]));
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new ClientHandler());
                        ch.pipeline().addLast(new StringEncoder());
                    }
                });
        CuratorFramework client = ZookeeperFactory.getClient();
        String host = "localhost";
        int port = 7788;
        try {
            List<String> serverPaths = client.getChildren().forPath(Constants.SERVER_REGISTER_PATH.getValue());

            // 客户端加上ZK,监听服务器的变化
            CuratorWatcher watcher = new ServerWatcher();
            client.getChildren().usingWatcher(watcher).forPath(Constants.SERVER_REGISTER_PATH.getValue());

            for (String path: serverPaths) {
                String [] array = path.split("#");
                ChannelManager.realServerPath.add(array[0]+"#"+array[1]);
                ChannelFuture channelFuture = b.connect(array[0], Integer.valueOf(array[1]));
                ChannelManager.addChannelAndPath(path, channelFuture);
            }

        } catch (Exception e) {
            logger.error("connect to server error", e);
        }
    }

    // every request is connected to one channel, one channel can handle multiple requests
    public static Response send(ClientRequest request){
        ChannelFuture f = ChannelManager.get(ChannelManager.position);
        if (f == null) {
            logger.error("channel is null or closed");
            return null;
        }
        f.channel().writeAndFlush(JSONObject.toJSONString(request)+"\r\n");
        Long timeout = 60L;
        ResultFuture future = new ResultFuture(request);
        return future.get(timeout);
    }

}
