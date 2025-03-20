package com.SASS.registry;

import com.SASS.registry.core.ServiceRegistry;
import com.SASS.registry.loadbalance.LoadBalancer;
import com.SASS.registry.model.ServiceData;
import com.SASS.registry.routing.WeightedStrategy;
import com.SASS.registry.zookeeper.ZooKeeperManager;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author 曾志鹏
 * @date 2025/3/7 18:58
 */
public class RouteTest {
    @Test
    public void test() throws IOException, InterruptedException, KeeperException {
        String host = "192.168.19.100:2181,192.168.19.101:2181,192.168.19.102:2181";
        ZooKeeper zk = new ZooKeeperManager().connect(host,3000);
        ServiceRegistry registry = new ServiceRegistry(zk);
        List<ServiceData> servers = registry.discoverServers("102");
        assertEquals(2, servers.size(), "应发现两个支持 102 的服务实例");

        // 使用权重策略路由
        LoadBalancer balancer = new LoadBalancer(new WeightedStrategy());
        String target = balancer.route(servers);
        System.out.println(target);
    }
}
