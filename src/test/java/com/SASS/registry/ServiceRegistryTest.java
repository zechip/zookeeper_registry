package com.SASS.registry;

import com.SASS.registry.core.ServiceRegistry;
import com.SASS.registry.core.ServiceWatcher;
import com.SASS.registry.loadbalance.LoadBalancer;
import com.SASS.registry.model.ServiceData;
import com.SASS.registry.routing.RoutingStrategy;
import com.SASS.registry.routing.WeightedStrategy;
import com.SASS.registry.zookeeper.ZooKeeperManager;
import org.apache.zookeeper.ZooKeeper;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ServiceRegistryTest {

    private static ZooKeeper zk;
    private static ServiceRegistry registry;
    private static LoadBalancer loadBalancer;

    @BeforeAll
    static void setup() throws Exception {
        // 初始化 ZooKeeper
        ZooKeeperManager zkManager = new ZooKeeperManager();
        String host = "192.168.19.100:2181,192.168.19.101:2181,192.168.19.102:2181"; // 使用用户提供的 ZooKeeper 地址
        zk = zkManager.connect(host, 3000);
        // 初始化服务注册中心和监听器
        registry = new ServiceRegistry(zk);
        new ServiceWatcher(zk, registry);
        // 初始化负载均衡器
        RoutingStrategy strategy = new WeightedStrategy();
        loadBalancer = new LoadBalancer(strategy);
    }

    @BeforeEach
    void beforeEach() throws Exception {
        // 清理之前测试可能残留的数据
        registry.initPaths();
    }

    @Test
    void testRegisterService() throws Exception {
        // 注册服务
        registry.registerService("AI-1", "192.168.1.100", 8080, List.of("function1", "function2"), 5, 20, 30);
        registry.registerService("AI-2", "192.168.1.101", 8080, List.of("function1", "function3"), 3, 60, 50);

        // 验证服务节点是否存在
        assertNotNull(zk.exists("/services/AI-1", false));
        assertNotNull(zk.exists("/services/AI-2", false));

        // 验证功能节点是否存在
        assertNotNull(zk.exists("/functions/function1", false));
        assertNotNull(zk.exists("/functions/function2", false));
        assertNotNull(zk.exists("/functions/function3", false));
    }

    @Test
    void testDiscoverServers() throws Exception {
        // 注册服务
        registry.registerService("AI-1", "192.168.1.100", 8080, List.of("function1", "function2"), 5, 20, 30);
        registry.registerService("AI-2", "192.168.1.101", 8080, List.of("function1", "function3"), 3, 60, 50);

        // 发现支持 function1 的服务
        List<ServiceData> servers = registry.discoverServers("function1");
        assertEquals(2, servers.size(), "应发现两个支持 function1 的服务实例");

        // 发现支持 function2 的服务
        servers = registry.discoverServers("function2");
        assertEquals(1, servers.size(), "应发现一个支持 function2 的服务实例");

        // 发现支持 function3 的服务
        servers = registry.discoverServers("function3");
        assertEquals(1, servers.size(), "应发现一个支持 function3 的服务实例");
    }

    @Test
    void testLoadBalancing() throws Exception {
        // 注册服务
        registry.registerService("AI-1", "192.168.1.100", 8080, List.of("function1"), 5, 20, 30);
        registry.registerService("AI-2", "192.168.1.101", 8080, List.of("function1"), 3, 60, 50);

        // 发现服务并进行负载均衡
        List<ServiceData> servers = registry.discoverServers("function1");
        String targetServer = loadBalancer.route(servers);

        // 验证返回结果
        assertTrue(targetServer.equals("AI-1") || targetServer.equals("AI-2"), "负载均衡应返回有效的服务实例");
    }

    @Test
    void testServiceDownAndCleanup() throws Exception {
        // 注册服务
        registry.registerService("AI-1", "192.168.1.100", 8080, List.of("function1"), 5, 20, 30);
        registry.registerService("AI-2", "192.168.1.101", 8080, List.of("function1"), 3, 60, 50);

        // 模拟服务下线
        zk.delete("/services/AI-2", -1);

        // 等待监听器触发清理逻辑
        TimeUnit.SECONDS.sleep(3);

        // 验证功能标签节点是否已清理
        List<String> servers = registry.getServerIds("function1");
        assertEquals(1, servers.size(), "功能标签节点应仅剩一个服务实例");
        assertEquals("AI-1", servers.get(0), "剩余服务应为 AI-1");
    }

    @AfterEach
    void afterEach() throws Exception {
        // 清理测试数据
        deleteNodeIfExists("/services/AI-1");
        deleteNodeIfExists("/services/AI-2");
        deleteNodeIfExists("/functions/function1");
        deleteNodeIfExists("/functions/function2");
        deleteNodeIfExists("/functions/function3");
    }

    private void deleteNodeIfExists(String path) throws Exception {
        if (zk.exists(path, false) != null) {
            zk.delete(path, -1);
        }
    }

    @AfterAll
    static void cleanup() throws Exception {
        // 关闭 ZooKeeper 连接
        if (zk != null) {
            zk.close();
        }
    }
}