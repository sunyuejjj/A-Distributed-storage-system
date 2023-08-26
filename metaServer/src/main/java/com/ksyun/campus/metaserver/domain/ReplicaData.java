package com.ksyun.campus.metaserver.domain;

import lombok.Data;

import java.util.UUID;

@Data
public class ReplicaData {
    public String id;
    public String dsNode; //dataserver node
    public String path;

    public ReplicaData(String dsNode){
        this.dsNode = dsNode;
    }
    public ReplicaData(){}
}
