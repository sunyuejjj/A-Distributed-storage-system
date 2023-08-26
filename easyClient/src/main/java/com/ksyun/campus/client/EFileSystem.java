package com.ksyun.campus.client;

import com.ksyun.campus.client.domain.ClusterInfo;
import com.ksyun.campus.client.domain.ReplicaData;
import com.ksyun.campus.client.domain.StatInfo;
import com.ksyun.campus.client.dto.BlockData;
import com.ksyun.campus.client.dto.DataServerMsg;
import com.ksyun.campus.client.util.ZKUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
public class EFileSystem extends FileSystem{
    private String fileSystem="/dbp";
    private String fileName="default";
    private ClusterInfo clusterinfo;
    private ZKUtil zkUtil;
    private BlockData blockdata;
    private RestTemplate restTemplate;
    public EFileSystem() {
        blockdata = new BlockData();
        zkUtil = new ZKUtil();
        restTemplate = new RestTemplate();
    }

    public EFileSystem(String fileSystem){
        this.fileSystem = fileSystem;
    }
    public FSInputStream open(String path) throws IOException {
        List<ClusterInfo.MetaServerMsg> metaserverlist = zkUtil.getMetaServerList();
        //连接metaserver，拿到数据的id和ip
        String metaurl = "http://" + metaserverlist.get(0).getHost() + ":" +
                metaserverlist.get(0).getPort() + "/stats?fileSystem=" + fileSystem + "&path=" + path;
        ResponseEntity<StatInfo> responseEntity = restTemplate.exchange(
                metaurl,
                HttpMethod.GET,
                null,
                StatInfo.class
        );
        StatInfo item = responseEntity.getBody();
//        System.out.println(item.getSize());
        if(item==null){
            System.out.println("文件不存在，读取失败");
            return null;
        }else{
            return new FSInputStream(item);
        }
    }
    public FSOutputStream create(String path){
        List<ClusterInfo.MetaServerMsg> metaserverlist = zkUtil.getMetaServerList();
        String metaurl = "http://" + metaserverlist.get(0).getHost() + ":" +
                metaserverlist.get(0).getPort() + "/create?fileSystem=" + fileSystem + "&path=" + path;
        ResponseEntity<Boolean> response = restTemplate.getForEntity(metaurl, Boolean.class);
        if(metaserverlist.size()==2){
            String metaurl_slaver = "http://" + metaserverlist.get(1).getHost() + ":" +
                    metaserverlist.get(1).getPort() + "/create?fileSystem=" + fileSystem + "&path=" + path;
            ResponseEntity<Boolean> response_slaver = restTemplate.getForEntity(metaurl_slaver, Boolean.class);
        }
        if(Objects.isNull(response)){
            log.error("文件创建失败，文件已经存在");
            return null;
        }
        else{
            return new FSOutputStream(path,fileSystem);
        }
    }
    public boolean mkdir(String path){
        List<ClusterInfo.MetaServerMsg> metaserverlist = zkUtil.getMetaServerList();
        String metaurl = "http://" + metaserverlist.get(0).getHost() + ":" +
                metaserverlist.get(0).getPort() + "/mkdir?fileSystem=" + fileSystem + "&path=" + path;
        ResponseEntity<Boolean> response = restTemplate.getForEntity(metaurl, Boolean.class);
        if(metaserverlist.size()==2){
            String metaurl_slaver = "http://" + metaserverlist.get(1).getHost() + ":" +
                    metaserverlist.get(1).getPort() + "/mkdir?fileSystem=" + fileSystem + "&path=" + path;
            ResponseEntity<Boolean> response_slaver = restTemplate.getForEntity(metaurl_slaver, Boolean.class);
        }
        return response.getBody();
    }
    public boolean delete(String path){
        List<ClusterInfo.MetaServerMsg> metaserverlist = zkUtil.getMetaServerList();
        String metaurl = "http://" + metaserverlist.get(0).getHost() + ":" +
                metaserverlist.get(0).getPort() + "/delete?fileSystem=" + fileSystem + "&path=" + path;
        ResponseEntity<Boolean> response = restTemplate.getForEntity(metaurl, Boolean.class);
        if(metaserverlist.size()==2){
            String metaurl_slaver = "http://" + metaserverlist.get(1).getHost() + ":" +
                    metaserverlist.get(1).getPort() + "/delete?fileSystem=" + fileSystem + "&path=" + path;
            ResponseEntity<Boolean> response_slaver = restTemplate.getForEntity(metaurl_slaver, Boolean.class);
        }
        return response.getBody();
    }
    public StatInfo getFileStats(String path){
        List<ClusterInfo.MetaServerMsg> metaserverlist = zkUtil.getMetaServerList();
        String metaurl = "http://" + metaserverlist.get(0).getHost() + ":" +
                metaserverlist.get(0).getPort() + "/stats?fileSystem=" + fileSystem + "&path=" + path;
        ResponseEntity<StatInfo> response = restTemplate.getForEntity(metaurl, StatInfo.class);
        return response.getBody();
    }
    public List<StatInfo> listFileStats(String path){
        List<ClusterInfo.MetaServerMsg> metaserverlist = zkUtil.getMetaServerList();
        //连接metaserver，拿到数据的id和ip
        String metaurl = "http://" + metaserverlist.get(0).getHost() + ":" +
                metaserverlist.get(0).getPort() + "/listdir?fileSystem=" + fileSystem + "&path=" + path;
        ResponseEntity<List<StatInfo>> responseEntity = restTemplate.exchange(
                metaurl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<StatInfo>>() {}
        );
        return responseEntity.getBody();
    }
    public ClusterInfo getClusterInfo(){
        return zkUtil.getClusterInfo();
    }
}
