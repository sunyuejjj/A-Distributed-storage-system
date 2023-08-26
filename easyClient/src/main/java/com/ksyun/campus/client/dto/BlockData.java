package com.ksyun.campus.client.dto;

import com.ksyun.campus.client.domain.ClusterInfo;
import lombok.Data;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class BlockData implements Serializable {
    List<DataServerMsg> dataservermsgs;
    Integer length;
    String blockid;
    byte[] data;
    @PostConstruct
    public void init()
    {
        dataservermsgs = new ArrayList<>();
    }

}
