package com.ksyun.campus.client.util;

import com.ksyun.campus.client.domain.ClusterInfo;
import com.ksyun.campus.client.dto.DataServerMsg;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
public class ZKUtil {
    private String zkclient;
    private CuratorFramework client;
    private String dataparentNodePath = "/dataServer";
    private String metaparentNodePath = "/metaServer";
    private String masternodepath = "/metaServer-master";
    private String slavernodepath = "/metaServer-slaver";
    public ZKUtil(){
        this.client = CuratorFrameworkFactory.newClient(
                "10.0.0.201:2181", // ZooKeeper连接字符串
                new ExponentialBackoffRetry(1000, 3) // 重试策略
        );
        client.start();
    }
    public ClusterInfo.MetaServerMsg getMastermetaserver(){
        return this.getMetaServerList().get(0);
    }
    public List<ClusterInfo.MetaServerMsg> getMetaServerList(){
        List<String> childreniplist = new ArrayList<>();
        try {
            String chileAddress = new String(client.getData().forPath(metaparentNodePath+masternodepath));
            childreniplist.add(chileAddress);
        }catch (Exception e){
            log.error("master-metaserver获取失败");
        }
        try {
            String chileAddress = new String(client.getData().forPath(metaparentNodePath+slavernodepath));
            childreniplist.add(chileAddress);
        }catch (Exception e){
            log.error("slaver-metaserver获取失败");
        }
        List<ClusterInfo.MetaServerMsg> res = new ArrayList<>();
        for (String str : childreniplist) {
            String ip = MyStringUtil.splitstr(str,":").get(0);
            Integer port = Integer.valueOf(MyStringUtil.splitstr(str,":").get(1));
            res.add(new ClusterInfo.MetaServerMsg(ip, port));
        }
        return res;
    }
    public List<ClusterInfo.DataServerMsg> getDataServerList() {
        List<String> children = null;
        try {
            children = client.getChildren().forPath(dataparentNodePath);
            Random random = new Random();
            int num = children.size();
            for (int i = 0; i < num - 3; i++) {//仅保留三个Datasever，随机保留
                int randomIndex = random.nextInt(children.size());
                children.remove(randomIndex);
            }
        } catch (Exception e) {
            log.error("dataserver列表获取失败");
        }
        List<String> childreniplist = new ArrayList<>();
        for(String str : children){
            try {
                String chileAddress = new String(client.getData().forPath(dataparentNodePath+"/"+str));
                childreniplist.add(chileAddress);
            }catch (Exception e){
                log.error("dataserver列表转化失败");
            }
        }
        List<ClusterInfo.DataServerMsg> res = new ArrayList<>();
        for (String str : childreniplist) {
            String ip = MyStringUtil.splitstr(str,":").get(0);
            Integer port = Integer.valueOf(MyStringUtil.splitstr(str,":").get(1));
            res.add(new ClusterInfo.DataServerMsg(ip, port));
        }
        return res;
    }
    public List<ClusterInfo.DataServerMsg> getallDataServerList() {
        List<String> children = null;
        try {
            children = client.getChildren().forPath(dataparentNodePath);
            Random random = new Random();
            int num = children.size();
            for (int i = 0; i < num - 4 ; i++) {//仅保留三个Datasever，随机保留
                int randomIndex = random.nextInt(children.size());
                children.remove(randomIndex);
            }
        } catch (Exception e) {
            log.error("dataserver列表获取失败");
        }
        List<String> childreniplist = new ArrayList<>();
        for(String str : children){
            try {
                String chileAddress = new String(client.getData().forPath(dataparentNodePath+"/"+str));
                childreniplist.add(chileAddress);
            }catch (Exception e){
                log.error("dataserver列表转化失败");
            }
        }
        List<ClusterInfo.DataServerMsg> res = new ArrayList<>();
        for (String str : childreniplist) {
            String ip = MyStringUtil.splitstr(str,":").get(0);
            Integer port = Integer.valueOf(MyStringUtil.splitstr(str,":").get(1));
            res.add(new ClusterInfo.DataServerMsg(ip, port));
        }
        return res;
    }
    public ClusterInfo getClusterInfo(){
        List<ClusterInfo.MetaServerMsg> metaServerMsgList = this.getMetaServerList();
        List<ClusterInfo.DataServerMsg> dataServerMsgList = this.getDataServerList();
        ClusterInfo res = new ClusterInfo();
        if(metaServerMsgList.size()==0){
            System.out.println("metaserver列表获取失败");
            return null;
        }else if(metaServerMsgList.size()==1){
            res.setMasterMetaServer(metaServerMsgList.get(0));
        }else{
            res.setMasterMetaServer(metaServerMsgList.get(0));
            res.setSlaveMetaServer(metaServerMsgList.get(1));
        }
        res.setDataServer(this.getallDataServerList());
        return res;
    }
}