package com.SASS.registry.model;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * @author 曾志鹏
 * @date 2025/3/3 16:28
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceData {
    private String serverId;
    private String ip;
    private int port;
    private List<String> functions;
    private int weight;
    private int cpuLoad;
    private int memoryLoad;

    // 默认构造函数（必须添加）
    public ServiceData() {}

    // 原有带参数的构造函数
    public ServiceData(String serverId, String ip, int port,
                       List<String> functions, int weight,
                       int cpuLoad, int memoryLoad) {
        this.serverId = serverId;
        this.ip = ip;
        this.port = port;
        this.functions = functions;
        this.weight = weight;
        this.cpuLoad = cpuLoad;
        this.memoryLoad = memoryLoad;
    }

    // Getter 和 Setter 方法（必须存在）
    public String getServerId() { return serverId; }
    public void setServerId(String serverId) { this.serverId = serverId; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public List<String> getFunctions() { return functions; }
    public void setFunctions(List<String> functions) { this.functions = functions; }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public int getCpuLoad() { return cpuLoad; }
    public void setCpuLoad(int cpuLoad) { this.cpuLoad = cpuLoad; }

    public int getMemoryLoad() { return memoryLoad; }
    public void setMemoryLoad(int memoryLoad) { this.memoryLoad = memoryLoad; }
}