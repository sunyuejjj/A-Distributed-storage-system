package com.ksyun.campus.dataserver.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DataServerMsg  implements Serializable {
    String ip;
    Integer port;
}