package com.occi.org.client.core;
import com.occi.org.client.factory.ZookeeperFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @description:
 * @author: occi
 * @date: 2024/10/5
 */
public class ServerWatcher implements CuratorWatcher {
    // 服务器列表发生变化时，触发该方法
    @Override
    public void process(WatchedEvent event) throws Exception {
        CuratorFramework client = ZookeeperFactory.getClient();
        String path = event.getPath();

        // 重新注册 watcher，确保下次事件也能捕捉到
        client.getChildren().usingWatcher(this).forPath(path);

        // 获取最新的服务器列表
        List<String> newServerPaths = client.getChildren().forPath(path);

        // 保存需要新增和移除的服务器列表
        Set<String> newServerSet = new HashSet<>();
        for (String serverPath : newServerPaths) {
            String[] strings = serverPath.split("#");
            newServerSet.add(strings[0] + "#" + strings[1]);
        }

        // 查找需要移除的服务器连接
        Set<String> currentServerSet = new HashSet<>(ChannelManager.realServerPath);
        currentServerSet.removeAll(newServerSet);

        // 移除失效的服务器连接
        for (String oldServer : currentServerSet) {
            ChannelManager.removeChannelByServerPathAndRemovePath(oldServer);
        }

        // 对于新的服务器，建立新的连接, 路径添加到realServerPath中;
        // 注意：不会进行重复连接
        for (String newServer : newServerSet) {
            if (!ChannelManager.realServerPath.contains(newServer)) {
                String[] strings = newServer.split("#");
                try {
                    ChannelFuture channelFuture = TCPClient.b.connect(strings[0], Integer.valueOf(strings[1]));
                    ChannelManager.addChannelAndPath(newServer, channelFuture);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
