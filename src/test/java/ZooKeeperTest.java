/**
 * @author 曾志鹏
 * @date 2025/3/3 17:20
 */

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 *作为客户端连接集群
 */
public class ZooKeeperTest {
    //创建Zookeeper对象，通过该对象来连接集群
    private ZooKeeper zooKeeperClient;

    //集群ip地址,和指定客户端需要哪个端口连接（2181）
    private String connectionStr =
            "192.168.19.100:2181,192.168.19.101:2181,192.168.1.102:2181";
    //单位毫秒
    //在指定时间内连接集群，超出指定时间不再尝试，报错
    private final int sessionTimeout = 60*1000;
    //连接zookeeper集群前的初始化准备
    //@Test
    @Before//运行时机在其他测试方法之前被调用
    //这里要保证后台三台服务器正常运行状态
    public void init() throws IOException {
        //创建监听器，监听节点数据变化或者子节点变化
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("监听事件执行。。。");
            }
        };
        zooKeeperClient = new ZooKeeper(connectionStr, sessionTimeout, watcher);
    }
    @Test
    public void createNode() throws InterruptedException, KeeperException {
        //create方法：表示在指定节点下创建一个新节点
        /*
         * 参数1：创建的节点名称 需要指定完整的父节点
         * 参数2：创建该节点同时初始化的节点数据
         * 参数3：节点权限
         * 参数4：节点的类型
         */
        zooKeeperClient.create(
                "/zzp",
                "曾志鹏".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT
        );
    }

    //查询节点
    @Test
    public void find() throws InterruptedException, KeeperException {
        /*
         *参数：
         * 1、被查询的节点名称(完整路径）
         * 2、boolean数值 false：不需要
         * 3、接收一个Stat类对象，可以直接new一个(多用于需要获取节点元数据的时候，否则可以直接传入null)
         *  */
        byte[] data = zooKeeperClient.getData("/zzp",false,new Stat());
        String content =new String(data);
        System.out.println("content = "+content);
    }

    //修改节点
    @Test
    public void update() throws InterruptedException, KeeperException {
        /*参数：
         * 1、被修改的节点的名称 完整路径
         * 2、想存入的新数据 要转化为字节数组
         * 3、dataVersion = 0 :数据变化的版本号0->n
         * 返回一个Stat类型对象 表示当前节点的元数据 输出节点的详细信息
         *  */
        Stat stat = zooKeeperClient.setData("/zzp","鹏志曾".getBytes(),0);
        System.out.println("stat = " + stat);
    }

    //删除
    @Test
    public void delete() throws InterruptedException, KeeperException {
        zooKeeperClient.delete("/zzp", 1);
        System.out.println("删除成功");
    }

    //判断是否存在
    @Test
    public void exist() throws InterruptedException, KeeperException {
        Stat stat = zooKeeperClient.exists("/com/zookeeper",false);
        System.out.println("stat = "+ stat);
        String flag = stat == null ? "节点不存在":"节点存在";
        System.out.println(flag);
    }

    //获取子节点
    @Test
    public void getChildren() throws InterruptedException, KeeperException {
        List<String> node_list = zooKeeperClient.getChildren("/cn",false);
        for (String node : node_list){
            System.out.println(node);
        }
    }

    //监听节点变化
    @Test
    public void listenChildrenNodes() throws InterruptedException, KeeperException, IOException {
        List<String> node_list = zooKeeperClient.getChildren("/cn",true);
        for (String node : node_list){
            System.out.println(node);
        }
//        try {
//            System.in.read();//线程在当前处 处于等待状态
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        while(true);
    }
}
