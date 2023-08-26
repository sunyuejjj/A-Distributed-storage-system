package com.ksyun.campus.client.domain;

import java.util.ArrayList;
import java.util.List;

public class ClusterInfo {
    private MetaServerMsg masterMetaServer;
    private MetaServerMsg slaveMetaServer;
    private List<DataServerMsg> dataServer;

    public ClusterInfo(){
        masterMetaServer = new MetaServerMsg();
        slaveMetaServer = new MetaServerMsg();
        dataServer = new ArrayList<>();
    }

    /**
     * 用字符串初始化 主metaserver
     * @param str
     */
    public void setMasterMetaServer(String str){
        String[] parts = str.split(":");
        masterMetaServer.setHost(parts[0]);
        masterMetaServer.setPort(Integer.valueOf(parts[1]));
    }
    /**
     * 用字符串初始化 从metaserver
     * @param str
     */
    public void setSlaveMetaServer(String str){
        String[] parts = str.split(":");
        slaveMetaServer.setHost(parts[0]);
        slaveMetaServer.setPort(Integer.valueOf(parts[1]));
    }
    public MetaServerMsg getMasterMetaServer() {
        return masterMetaServer;
    }
    public void setMasterMetaServer(MetaServerMsg masterMetaServer) {
        this.masterMetaServer = masterMetaServer;
    }

    public MetaServerMsg getSlaveMetaServer() {
        return slaveMetaServer;
    }

    public void setSlaveMetaServer(MetaServerMsg slaveMetaServer) {
        this.slaveMetaServer = slaveMetaServer;
    }

    public List<DataServerMsg> getDataServer() {
        return dataServer;
    }

    public void setDataServer(List<DataServerMsg> dataServer) {
        this.dataServer = dataServer;
    }

    public static class MetaServerMsg{
        private String host;
        private Integer port;

        public MetaServerMsg() {

        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public MetaServerMsg(String host, Integer port){this.host=host;this.port=port;}
    }
    public static class DataServerMsg{
        private String host;
        private Integer port;
        private Integer fileTotal;
        private Integer capacity;
        private Integer useCapacity;
        public DataServerMsg(String host, Integer port){
            this.host = host;
            this.port =port;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public int getFileTotal() {
            return fileTotal;
        }

        public void setFileTotal(int fileTotal) {
            this.fileTotal = fileTotal;
        }

        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public int getUseCapacity() {
            return useCapacity;
        }

        public void setUseCapacity(int useCapacity) {
            this.useCapacity = useCapacity;
        }
    }

    @Override
    public String toString() {
        return "ClusterInfo{" +
                "masterMetaServer=" + masterMetaServer +
                ", slaveMetaServer=" + slaveMetaServer +
                ", dataServer=" + dataServer +
                '}';
    }
}
