package com.SASS.registry.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * @author 曾志鹏
 * @date 2025/3/3 18:53
 * 监听服务下线 自动清理标签
 */
public class ServiceWatcher implements Watcher {
    private final ZooKeeper zk;
    private final ServiceRegistry registry;

    public ServiceWatcher(ZooKeeper zk, ServiceRegistry registry) {
        this.zk = zk;
        this.registry = registry;
        watchServices(); // 启动监听
    }

    /**
     * 监听/services路径的子节点变化
     */
    private void watchServices() {
        try {
            // 监听子节点变化事件
            zk.getChildren(ServiceRegistry.BASE_SERVICE_PATH, this, (rc, path, ctx, children) -> {
                // 重新注册监听
                watchServices();
                // 为每个子节点注册删除监听
                for (String serverId : children) {
                    watchServiceNode(serverId);
                }
            }, null);
        } catch (Exception e) {
            throw new RuntimeException("监听服务失败", e);
        }
    }

    /**
     * 监听单个服务节点的删除事件
     */
    private void watchServiceNode(String serverId) {
        String path = ServiceRegistry.BASE_SERVICE_PATH + "/" + serverId;//     /services/AI-1
        try {
            zk.exists(path, this, (rc, path1, ctx, stat) -> {
                if (rc == KeeperException.Code.NONODE.intValue()) {
                    // 节点被删除，触发清理逻辑
                    cleanupFunctionNodes(serverId);
                }
            }, null);
        } catch (Exception e) {
            throw new RuntimeException("监听服务节点失败", e);
        }
    }

    /**
     * 清理功能标签中的无效服务ID
     */
    public void cleanupFunctionNodes(String serverId) {
        try {
            List<String> functions = zk.getChildren(ServiceRegistry.BASE_FUNCTION_PATH, false);
            for (String func : functions) {
                String funcPath = ServiceRegistry.BASE_FUNCTION_PATH + "/" + func;
                Stat stat = new Stat();
                byte[] data = zk.getData(funcPath, false, stat);
                if (data != null && data.length > 0) {
                    List<String> servers = registry.getServerIds(func);
                    if (servers.remove(serverId)) {
                        zk.setData(funcPath, registry.getMapper().writeValueAsBytes(servers), stat.getVersion());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("清理功能标签失败", e);
        }
    }

    @Override
    public void process(WatchedEvent event) {
        if (event.getType() == Event.EventType.NodeChildrenChanged) {
            watchServices(); // 重新注册监听
        }
    }
}