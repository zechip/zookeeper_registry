package com.SASS.registry.routing;

import com.SASS.registry.model.ServiceData;

import java.util.List;

/**
 * @author 曾志鹏
 * @date 2025/3/5 18:43
 */
public interface RoutingStrategy {
    String selectServer(List<ServiceData> servers);
}
