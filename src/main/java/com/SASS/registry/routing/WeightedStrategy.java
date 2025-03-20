package com.SASS.registry.routing;

import com.SASS.registry.model.ServiceData;

import java.util.List;
import java.util.Random;

/**
 * @author 曾志鹏
 * @date 2025/3/5 18:44
 *  权重路由策略实现类
 */
public class WeightedStrategy implements RoutingStrategy {
    @Override
    public String selectServer(List<ServiceData> servers) {
        if (servers.isEmpty()) return null;

        // 计算总权重
        int totalWeight = servers.stream().mapToInt(ServiceData::getWeight).sum();
        int random = new Random().nextInt(totalWeight);
        int current = 0;

        for (ServiceData server : servers) {
            current += server.getWeight();
            if (random < current) {
                return server.getServerId();
            }
        }
        return null;
    }
}
