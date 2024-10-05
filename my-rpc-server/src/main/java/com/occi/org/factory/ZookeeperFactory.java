package com.occi.org.factory;

import com.occi.org.constants.Constants;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.occi.org.constants.Constants.ZK_NODE_PATH;

/**
 * @description: Zookeeper Factory
 * @author: occi
 * @date: 2024/9/30
 */
public class ZookeeperFactory {

    public static CuratorFramework client;

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperFactory.class);

    public static void main(String[] args) {
        try {
            String s = "balabala";
            CuratorFramework client = getClient();
            if (client != null) {
                client.create().forPath("/netty", s.getBytes());
                System.out.println("Node created successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CuratorFramework getClient() {
        if (client == null) {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3); // 重试机制
            client = CuratorFrameworkFactory.newClient("localhost:2181", retryPolicy); // 使用 2181 端口
            client.start();
            try {
                // 确保父节点 /netty 存在
                ensureNettyNodeExists(client);
            } catch (Exception e) {
                logger.error("Error ensuring " + ZK_NODE_PATH + " node exists: {}", e.getMessage());
            }
        }
        return client;
    }

    private static void ensureNettyNodeExists(CuratorFramework client) throws Exception {
        String nettyPath = ZK_NODE_PATH.getValue();
        if (client.checkExists().forPath(nettyPath) == null) {
            // 如果父节点 /netty 不存在，则创建
            client.create().creatingParentsIfNeeded().forPath(nettyPath);
            logger.info("Created parent node: " + nettyPath);
        } else {
            logger.info("Parent node " + nettyPath+ " already exists");
        }
    }
}
