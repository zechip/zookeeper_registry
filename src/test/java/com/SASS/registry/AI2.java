package com.SASS.registry;

import com.SASS.registry.core.ServiceRegistry;
import com.SASS.registry.zookeeper.ZooKeeperManager;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author 曾志鹏
 * @date 2025/3/7 12:01
 */
public class AI2 {
    @Test
    public void registry_ai2() throws IOException, InterruptedException, KeeperException {
        String host =  "192.168.19.100:2181,192.168.19.101:2181,192.168.19.102:2181";
        ZooKeeper zk = new ZooKeeperManager().connect(host,3000);
        ServiceRegistry registry = new ServiceRegistry(zk);
        registry.initPaths();
        registry.registerService("AI-2", "192.168.2.100", 8080, List.of("101", "103"), 5, 20, 30);
        while (true) {
            Thread.sleep(1000);
        }
    }
}
