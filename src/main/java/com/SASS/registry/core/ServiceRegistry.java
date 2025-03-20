package com.SASS.registry.core;

import com.SASS.registry.model.ServiceData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 曾志鹏
 * @date 2025/3/3 16:00
 * 实现服务和功能节点的注册和发现
 */


public class ServiceRegistry {
    public static final String BASE_SERVICE_PATH = "/services";
    public static final String BASE_FUNCTION_PATH = "/functions";

    private final ZooKeeper zk;
    private final ObjectMapper mapper = new ObjectMapper();

    public ServiceRegistry(ZooKeeper zk) throws InterruptedException, KeeperException {
        this.zk = zk;
        initPaths();
    }

    //确保节点存在。实例化zk的操作将交由ZooKeeperManager先行实现
    public void initPaths() {
        try{
            if(zk.exists(BASE_SERVICE_PATH,false) == null){
                zk.create(BASE_SERVICE_PATH,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            if(zk.exists(BASE_FUNCTION_PATH,false) == null){
                zk.create(BASE_FUNCTION_PATH,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException("初始化根节点失败，请重试  ",e);
        }
    }
    //注册服务实例实现功能标签节点内部数据的自动更新
    public void registerService(String serverId,
                                String ip,
                                int port,
                                List<String> functions,
                                int weight,
                                int cpuLoad,
                                int memoryLoad) throws IOException, InterruptedException, KeeperException {
        ServiceData data = new ServiceData(serverId,ip,port,functions,weight,cpuLoad,memoryLoad);
        //序列化
        String servicePath = BASE_SERVICE_PATH+"/"+serverId;
        byte[] dataBytes = mapper.writeValueAsBytes(data);
        Stat stat = zk.exists(servicePath,false);
        //服务实例节点的生成/更新（服务上线）
        if(stat == null){
            zk.create(servicePath,dataBytes, ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
        }
        else{
            zk.setData(servicePath,dataBytes,stat.getVersion());
        }
        for(String func:functions){
            updateFunctionNode(func,serverId);
        }
    }
    //实现更新标签
    private void updateFunctionNode(String functionId, String serverId) throws InterruptedException, KeeperException, IOException {
        String funcPath = BASE_FUNCTION_PATH+"/"+functionId;

        // 确保功能节点标签存在
        if(zk.exists(funcPath,false) == null){
            zk.create(funcPath,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
        }
        //Compare-And-Set机制更新
        retryUpdate(funcPath,serverId);
    }
    //重试更新功能标签节点（解决版本冲突）
    private void retryUpdate(String funcPath,String serverId) throws InterruptedException, KeeperException, IOException {
        Stat stat = new Stat();
        byte[] oldData = zk.getData(funcPath,false,stat);
        List<String> servers = (oldData != null && oldData.length >0) ?
                mapper.readValue(oldData, ArrayList.class):new ArrayList<String>();
        if(!servers.contains(serverId)){
            servers.add(serverId);
            byte[] newData = mapper.writeValueAsBytes(servers);
            try{
                zk.setData(funcPath,newData, stat.getVersion());
            } catch (Exception e) {
                retryUpdate(funcPath,serverId);
            }
        }
    }
    //发现支持对应功能的服务器（元数据）列表
    public List<ServiceData> discoverServers(String functionId) throws IOException, InterruptedException, KeeperException {
        List<String> serverIds = getServerIds(functionId);
        List<ServiceData> servers = new ArrayList<>();
        for(String serverId:serverIds){
            String serverPath = BASE_SERVICE_PATH+"/"+serverId;
            byte[] data = zk.getData(serverPath,false,null);
            servers.add(mapper.readValue(data, ServiceData.class));
        }
        return servers;
    }
    //获取支持对应功能的服务器的Id
    public List<String> getServerIds(String functionId) throws InterruptedException, KeeperException, IOException {
        String funcPath = BASE_FUNCTION_PATH+"/"+functionId;
        if(zk.exists(funcPath,false) == null){
            return new ArrayList<>();
        }
        else{
            byte[] data = zk.getData(funcPath,false,null);
            return (data != null && data.length>0) ?
                    mapper.readValue(data,ArrayList.class) : new ArrayList<String>();
        }
    }

    public ObjectMapper getMapper() {
        return mapper;
    }
}
