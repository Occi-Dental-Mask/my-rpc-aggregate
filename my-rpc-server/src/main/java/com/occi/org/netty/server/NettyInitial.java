package com.occi.org.netty.server;

import com.occi.org.factory.ZookeeperFactory;
import com.occi.org.netty.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @description:
 * @author: occi
 * @date: 2024/10/1
 */
@Component
public class NettyInitial implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        start();
    }

    public void start() {
        Logger logger = getLogger(NettyInitial.class);
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()[0]));
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new IdleStateHandler(20, 15, 10, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new ServerHandler());
                        ch.pipeline().addLast(new StringEncoder());
                    }
                });
        int port = 7999;
        try {

            ChannelFuture cf = serverBootstrap.bind(port).sync();
            InetAddress address = InetAddress.getLocalHost();
            logger.info("Server started at http://{}:{}", address.getHostAddress(), port);
            CuratorFramework client = ZookeeperFactory.getClient();
            if (client != null) {
                logger.info("Zookeeper client: {}", client);
                client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/netty/" + address.getHostAddress() + "#" + port + "#");
                logger.info("Zookeeper node created: /netty/{}#{}", address.getHostAddress(), port);
            }
            cf.channel().closeFuture().sync();
            logger.info("Server closed");
        } catch (InterruptedException e) {
            logger.error("Server start failed: {}", e.getMessage());
        } catch (UnknownHostException e) {
            logger.error("Unknown host: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Exception: {}", e.getMessage());
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
