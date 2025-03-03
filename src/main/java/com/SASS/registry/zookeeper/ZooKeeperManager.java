package com.SASS.registry.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author 曾志鹏
 * @date 2025/3/3 17:17
 */
public class ZooKeeperManager {
    private ZooKeeper zookeeper;
    //等待一个条件（连接成功）
    private final CountDownLatch connectionLatch = new CountDownLatch(1);

    //连接服务器
    public ZooKeeper connect(String host,int timeout) throws IOException, InterruptedException {
        //创建zk客户端
        zookeeper = new ZooKeeper(host, timeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if(event.getState() == Watcher.Event.KeeperState.SyncConnected){
                    connectionLatch.countDown();
                }
            }
        });
        connectionLatch.await();
        return zookeeper;
    }

    public void close() throws InterruptedException {
        if(zookeeper != null){
            zookeeper.close();
        }
    }
}