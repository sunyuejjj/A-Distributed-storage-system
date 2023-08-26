package com.ksyun.campus.client.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DataServerMsg  implements Serializable {
    String ip;
    Integer port;

    public DataServerMsg(String ip, Integer port){
        this.ip = ip;
        this.port = port;
    }

    public DataServerMsg() {}
}