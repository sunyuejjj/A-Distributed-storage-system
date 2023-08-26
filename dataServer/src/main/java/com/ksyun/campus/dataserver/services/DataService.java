package com.ksyun.campus.dataserver.services;

import com.ksyun.campus.dataserver.dto.BlockData;
import com.ksyun.campus.dataserver.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class DataService {
    @Value("${az.rack}")
    private String rack;
    BlockData blockData;
    @Autowired
    RestTemplate restTemplate;
    /**
     * 1.根据blockid将数据按照流的形式写入到文件中
     * @param data
     */
    public ApiResponse write(BlockData data){
        //将数据写入本地。
        try (FileOutputStream outputStream = new FileOutputStream(rack+data.getBlockid()+".bin")) {
            outputStream.write(data.getData()); // 写入一个块的数据
        } catch (IOException e) {
            e.printStackTrace();
        }
        data.getDataservermsgs().remove(0);
        if(data.getDataservermsgs().size()==0)
        {
            return new ApiResponse(200,"最后一个节点插入完毕");
        }

        String url = "http://" + data.getDataservermsgs().get(0).getIp()+":"+data.getDataservermsgs().get(0).getPort()+"/write";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<BlockData> requestEntity = new HttpEntity<>(data, headers);
        ResponseEntity<ApiResponse> responseEntity = restTemplate.postForEntity(url, requestEntity, ApiResponse.class);

        return responseEntity.getBody();
        //将数据写入远程
        //todo 写本地
        //todo 调用远程ds服务写接口，同步副本，已达到多副本数量要求
        //todo 选择策略，按照 az rack->zone 的方式选取，将三副本均分到不同的az下
        //todo 支持重试机制
        //todo 返回三副本位置
    }
    public BlockData read(String blockid ,Integer length){
        byte[] fileData = new byte[0];
        try (FileInputStream fis = new FileInputStream(rack+blockid+".bin")) {
            fileData = new byte[length];
            int bytesRead = fis.read(fileData);
            if (bytesRead != length) {
                System.err.println("Error reading the entire file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        blockData = new BlockData();
        blockData.setBlockid(blockid);
        blockData.setData(fileData);
        //todo 根据path读取指定大小的内容
        return blockData;
    }
}
