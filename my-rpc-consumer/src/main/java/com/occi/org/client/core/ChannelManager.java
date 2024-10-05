package com.occi.org.client.core;

import io.netty.channel.ChannelFuture;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 管理多个Channel
 * @author: occi
 * @date: 2024/10/5
 */
public class ChannelManager {
    // 用于存储服务器通道
    public static CopyOnWriteArrayList<ChannelFuture> channelFutures = new CopyOnWriteArrayList<>();

    // 用于存储服务器路径（如 "localhost#8080"）
    public static CopyOnWriteArrayList<String> realServerPath = new CopyOnWriteArrayList<>();

    // 新增一个映射：服务器路径 -> ChannelFuture
    public static ConcurrentHashMap<String, ChannelFuture> serverPathToChannelMap = new ConcurrentHashMap<>();

    public static AtomicInteger position = new AtomicInteger(0); // 轮询发送

    // 按照 ChannelFuture 移除通道
    public static void removeChannel(ChannelFuture channel) {
        channelFutures.remove(channel);
        serverPathToChannelMap.values().remove(channel);
    }

    // 根据服务器路径移除通道
    public static void removeChannelByServerPathAndRemovePath(String serverPath) {
        ChannelFuture channel = serverPathToChannelMap.get(serverPath);
        realServerPath.remove(serverPath);
        if (channel != null) {
            removeChannel(channel);
        }
    }

    // 添加通道时,同时将服务器路径和通道进行关联
    public static void addChannelAndPath(String serverPath, ChannelFuture channel) {
        channelFutures.add(channel);
        realServerPath.add(serverPath);
        serverPathToChannelMap.put(serverPath, channel);
    }

    // 清空所有通道
    public static void clearChannel() {
        channelFutures.clear();
        serverPathToChannelMap.clear();
        realServerPath.clear();
    }

    // 轮询获取 ChannelFuture
    public static ChannelFuture get(AtomicInteger i) {
        ChannelFuture channelFuture = null;
        int size = channelFutures.size();
        if (i.get() >= size) {
            channelFuture = channelFutures.get(0);
            ChannelManager.position = new AtomicInteger(1);
        } else {
            channelFuture = channelFutures.get(i.getAndIncrement());
        }
        return channelFuture;
    }
}
