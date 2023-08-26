package com.ksyun.campus.metaserver.util;

import com.ksyun.campus.metaserver.domain.ReplicaData;
import com.ksyun.campus.metaserver.dto.DataServerMsg;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Slf4j
public class ZKUtil {
    private String zkclient = "10.0.0.201:2181";
    private CuratorFramework client;

    private String parentNodePath = "/dataServer";
    public ZKUtil(){
        this.client = CuratorFrameworkFactory.newClient(
                zkclient, // ZooKeeper连接字符串
                new ExponentialBackoffRetry(1000, 3) // 重试策略
        );
        client.start();
    }

    public List<ReplicaData> getDataServerList() {
        List<String> children = null;
        try {
            children = client.getChildren().forPath(parentNodePath);
            Random random = new Random();
            int num = children.size();
            for (int i = 0; i < num - 3; i++) {//仅保留三个Datasever，随机保留
                int randomIndex = random.nextInt(children.size());
                children.remove(randomIndex);
            }
        } catch (Exception e) {
            log.error("dataserver列表获取失败");
        }
        System.out.println(this.zkclient);
        List<String> childreniplist = new ArrayList<>();
        for(String str : children){
            try {
                String chileAddress = new String(client.getData().forPath(parentNodePath+"/"+str));
                childreniplist.add(chileAddress);
            }catch (Exception e){
                log.error("dataserver列表转化失败");
            }
        }
        List<ReplicaData> res = new ArrayList<>();
        for (String str : childreniplist) {
            res.add(new ReplicaData(str));
        }
        return res;
    }
}
