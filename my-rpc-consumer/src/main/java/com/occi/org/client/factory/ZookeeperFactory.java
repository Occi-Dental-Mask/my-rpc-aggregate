package com.occi.org.client.factory;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @description: Zookeeper Factory
 * @author: occi
 * @date: 2024/10/05
 */
public class ZookeeperFactory {

    public static CuratorFramework client;

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
            client = CuratorFrameworkFactory.newClient("localhost:2181", retryPolicy); // 使用 2181
            client.start();
        }
        return client;
    }
}
