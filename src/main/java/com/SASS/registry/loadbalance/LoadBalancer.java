package com.SASS.registry.loadbalance;

import com.SASS.registry.model.ServiceData;
import com.SASS.registry.routing.RoutingStrategy;

import java.util.List;

/**
 * @author 曾志鹏
 * @date 2025/3/6 15:58
 */
public class LoadBalancer {
    private final RoutingStrategy strategy;

    public LoadBalancer(RoutingStrategy strategy) {
        this.strategy = strategy;
    }

    public String route(List<ServiceData> servers) {
        return strategy.selectServer(servers);
    }
}