package com.ksyun.campus.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksyun.campus.client.domain.ReplicaData;
import com.ksyun.campus.client.domain.StatInfo;
import com.ksyun.campus.client.dto.BlockData;
import com.ksyun.campus.client.dto.DataServerMsg;
import com.ksyun.campus.client.response.ApiResponse;
import com.ksyun.campus.client.util.MyStringUtil;
import com.ksyun.campus.client.util.ZKUtil;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FSInputStream extends InputStream {
    private StatInfo statinfo;
    FileInputStream fsInputStream;
    FileOutputStream fsOutputStream;
    RestTemplate restTemplate;
    ZKUtil zkUtil;

    FSInputStream(StatInfo statinfo) throws IOException {
        restTemplate = new RestTemplate();
        this.statinfo = statinfo;
        zkUtil = new ZKUtil();
        writeToLocal();
    }

    private void writeToLocal() throws IOException {
        List<ReplicaData> itemList = statinfo.getReplicaData();
        //从dataserver拿到数据
        String dataurl = "http://" + itemList.get(0).dsNode+"/read?blockid="+ itemList.get(0).id + "&length=" +statinfo.getSize();
//        System.out.println(dataurl);
        ResponseEntity<BlockData> responseEntity1 = restTemplate.exchange(
                dataurl,
                HttpMethod.GET,
                null,
                BlockData.class
        );
        byte[] data = responseEntity1.getBody().getData();
        fsOutputStream = new FileOutputStream(responseEntity1.getBody().getBlockid()+"bin");
        fsOutputStream.write(data);
        fsInputStream = new FileInputStream(responseEntity1.getBody().getBlockid()+"bin");
    }
    @Override
    public int read() throws IOException {
        return 0;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return fsInputStream.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return fsInputStream.read(b, off, len);
    }

    @Override
    public void close() throws IOException {
        fsInputStream.close();
    }
}
