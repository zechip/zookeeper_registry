package com.SASS.registry;
import com.SASS.registry.core.ServiceRegistry;
import com.SASS.registry.core.ServiceWatcher;
import com.SASS.registry.model.ServiceData;
import com.SASS.registry.zookeeper.ZooKeeperManager;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author 曾志鹏
 * @date 2025/3/7 12:39
 */
public class WatcherTest {
    @Test
    public void test() throws IOException, InterruptedException, KeeperException {
        // 设置默认日志级别为WARNING
        Logger.getLogger("").setLevel(Level.WARNING);

        // 设置ZooKeeper库的日志级别为WARNING
        Logger zooKeeperLogger = Logger.getLogger("org.apache.zookeeper");
        zooKeeperLogger.setLevel(Level.WARNING);

        // 设置ZooKeeper客户端连接日志级别
        Logger clientCnxnLogger = Logger.getLogger("org.apache.zookeeper.ClientCnxn");
        clientCnxnLogger.setLevel(Level.WARNING);

        String host = "192.168.19.100:2181,192.168.19.101:2181,192.168.19.102:2181";
        ZooKeeper zk = new ZooKeeperManager().connect(host, 3000);
        ServiceRegistry registry = new ServiceRegistry(zk);
        registry.initPaths();
        ServiceWatcher watcher = new ServiceWatcher(zk, registry);


        // 保持程序运行
        while (true) {
            Thread.sleep(1000); // 每隔1秒检查一次
        }
        // closeModal ZooKeeper 连接
        // zk.close();
    }
}