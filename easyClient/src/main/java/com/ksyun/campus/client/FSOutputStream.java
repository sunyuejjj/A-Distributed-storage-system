package com.ksyun.campus.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksyun.campus.client.domain.ClusterInfo;
import com.ksyun.campus.client.domain.ReplicaData;
import com.ksyun.campus.client.domain.StatInfo;
import com.ksyun.campus.client.dto.BlockData;
import com.ksyun.campus.client.dto.DataServerMsg;
import com.ksyun.campus.client.response.ApiResponse;
import com.ksyun.campus.client.util.MyStringUtil;
import com.ksyun.campus.client.util.ZKUtil;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class FSOutputStream extends OutputStream {
    String path;
    String fileSystem;
    String filename;
    FileOutputStream fileOutputStream;
    BlockData blockData;
    StatInfo statinfo;
    RestTemplate restTemplate;
    ZKUtil zkUtil;
    FSOutputStream(String path, String fileSystem){
        this.filename = MyStringUtil.splitlaststr(path,"/");//获取当前文件的名字
        this.path = path;
        this.blockData = new BlockData();
        try {
            fileOutputStream = new FileOutputStream(filename);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        restTemplate = new RestTemplate();
        this.fileSystem=fileSystem;
        zkUtil = new ZKUtil();
        statinfo = new StatInfo();
    }
    @Override
    public void write(int b) throws IOException {
        fileOutputStream.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        fileOutputStream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        fileOutputStream.write(b, off, len);
    }

    @Override
    public void close() throws IOException {
        fileOutputStream.close();
        FileInputStream fileInputStream = new FileInputStream(filename);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        byte[] fileBytes = byteArrayOutputStream.toByteArray();

        fileInputStream.close();
        byteArrayOutputStream.close();
        List<ClusterInfo.MetaServerMsg> metaserverlist = zkUtil.getMetaServerList();
        //想metaserver中写入数据的长度
        String metaurlwrite = "http://" + metaserverlist.get(0).getHost() + ":" +
                metaserverlist.get(0).getPort() + "/write?fileSystem=" + fileSystem + "&path=" + path + "&size=" + fileBytes.length;
        Boolean response = restTemplate.getForObject(metaurlwrite, Boolean.class);
        //如果slavermetaserver不为空，也写入slavermetasever
        if(metaserverlist.size()==2){
            String metaurlwrite_slaver = "http://" + metaserverlist.get(1).getHost() + ":" +
                    metaserverlist.get(1).getPort() + "/write?fileSystem=" + fileSystem + "&path=" + path + "&size=" + fileBytes.length;
            Boolean response_slaver = restTemplate.getForObject(metaurlwrite_slaver, Boolean.class);
        }
        //连接metaserver，拿到数据的id和ip
        String metaurl = "http://" + metaserverlist.get(0).getHost() + ":" +
                metaserverlist.get(0).getPort() + "/stats?fileSystem=" + fileSystem + "&path=" + path;
        ResponseEntity<StatInfo> responseEntity = restTemplate.exchange(
                metaurl,
                HttpMethod.GET,
                null,
                StatInfo.class
        );
        List<ReplicaData> itemList = responseEntity.getBody().getReplicaData();
        List<DataServerMsg> dataservermsgs = new ArrayList<>();
        for(ReplicaData re : itemList){
            List<String> strr = MyStringUtil.splitstr(re.dsNode,":");
            dataservermsgs.add(new DataServerMsg(strr.get(0),Integer.valueOf(strr.get(1))));
        }
        this.blockData.setDataservermsgs(dataservermsgs);
        this.blockData.setBlockid(itemList.get(0).id);//正常每个dataserver的id应该是不一样的，这里设置成一样的了
        this.blockData.setData(fileBytes);

        //连接dataserver，写入数据
        String dataurl = "http://" + blockData.getDataservermsgs().get(0).getIp()+":"+blockData.getDataservermsgs().get(0).getPort()+"/write";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(blockData);

        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);
        ResponseEntity<ApiResponse> responseentity = restTemplate.postForEntity(dataurl, requestEntity, ApiResponse.class);
    }
}
