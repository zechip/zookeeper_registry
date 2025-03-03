package com.SASS.registry.model;

import java.util.List;

/**
 * @author 曾志鹏
 * @date 2025/3/3 16:28
 */
public class ServiceData {
    private String serverId;
    private String ip;
    private int port;
    private List<String> functions;
    private int weight;
    private int cpuLoad;
    private int memoryLoad;

    public ServiceData(String serverId,
                       String ip,
                       int port,
                       List<String> functions,
                       int weight,
                       int cpuLoad,
                       int memoryLoad)
    {
        this.cpuLoad = cpuLoad;
        this.ip = ip;
        this.functions = functions;
        this.port = port;
        this.weight =weight;
        this.memoryLoad = memoryLoad;
        this.serverId = serverId;
    }

    public int getCpuLoad() {
        return cpuLoad;
    }

    public void setCpuLoad(int cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    public List<String> getFunctions() {
        return functions;
    }

    public void setFunctions(List<String> functions) {
        this.functions = functions;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getMemoryLoad() {
        return memoryLoad;
    }

    public void setMemoryLoad(int memoryLoad) {
        this.memoryLoad = memoryLoad;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
