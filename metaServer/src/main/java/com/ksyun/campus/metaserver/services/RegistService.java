package com.ksyun.campus.metaserver.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PreDestroy;
import java.io.IOException;

@Component
@Slf4j
public class RegistService implements ApplicationRunner {
    @Value("${server.ipAddress}")
    private String ip;
    @Value("${server.port}")
    private Integer port;
    @Value("${server.childpath}")
    private String childpath;
    @Value("${server.parentpath}")
    private String parentpath;
    private ZooKeeper zookeeper;

    /**
     * 将实例信息注册到zokeeper
     * @throws IOException
     * @throws InterruptedException
     * @throws KeeperException
     */
    public void registToCenter() throws IOException, InterruptedException, KeeperException {
        // todo 将本实例信息注册至zk中心，包含信息 ip、port
        String connectString = "10.0.0.201:2181"; // ZooKeeper 服务器地址
        int sessionTimeout = 5000; // 会话超时时间，单位为毫秒

        zookeeper = new ZooKeeper(connectString, sessionTimeout, null);

        Stat nodeStat = zookeeper.exists(parentpath, false);
        if (nodeStat != null) {
            log.info("Node exists, version: " + nodeStat.getVersion());
        } else {
            byte[] parentData = "Parent Node Data".getBytes(); // 父节点数据
            CreateMode parentCreateMode = CreateMode.PERSISTENT; // 父亲节点不允许为临时节点
            zookeeper.create(parentpath, parentData, ZooDefs.Ids.OPEN_ACL_UNSAFE, parentCreateMode);
        }

        String childPath = parentpath+childpath; // 子节点路径（在父节点下）
        Stat childnodeStat = zookeeper.exists(childPath, false);
         //创建子节点
        if (childnodeStat != null) {
            log.info("Node exists, version: " + childnodeStat.getVersion());
        } else {
            log.info("节点不存在");
            byte[] childData = (ip+":"+port).getBytes(); // 子节点数据
            CreateMode childCreateMode = CreateMode.EPHEMERAL; // 创建持久节点
            zookeeper.create(childPath, childData, ZooDefs.Ids.OPEN_ACL_UNSAFE, childCreateMode);
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        registToCenter();
    }
    /**
     * 在服务销毁前进行一些操作
     * @throws InterruptedException
     */
    @PreDestroy
    public void cleanUp() throws InterruptedException {
        zookeeper.close();
    }

}
