package com.ksyun.campus.metaserver.services;

import com.ksyun.campus.metaserver.domain.ReplicaData;
import com.ksyun.campus.metaserver.dto.BlockData;
import com.ksyun.campus.metaserver.dto.DataServerMsg;
import com.ksyun.campus.metaserver.util.FileSystem;
import com.ksyun.campus.metaserver.util.MyStringUtil;
import com.ksyun.campus.metaserver.util.ZKUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;

@Service
public class MetaService {
    @Autowired
    private FileSystem filesystem;
    @Autowired
    private ZKUtil zkutil;
    @Autowired
    public Object pickDataServer(){

        // todo 通过zk内注册的ds列表，选择出来一个ds，用来后续的wirte，改进：这个是仿照hadoop将三个副本都找出来
        // 需要考虑选择ds的策略？负载
        return null;
    }
    public Object createFile(String fileSystem, String path){
        List<String> pathlist = MyStringUtil.splitstr(path,"/");
        Object res = filesystem.create(fileSystem, pathlist,path,"");
        return res;
    }

    public Object stats(String fileSystem,String path){
        List<String> pathlist = MyStringUtil.splitstr(path,"/");
        Object res = filesystem.stats(fileSystem, pathlist);
        if(Objects.nonNull(res)){
            return res;
        }
        else{
            return "not exist that file or dir!";
        }
    }

    public Object mkdir(String fileSystem,String path){
        List<String> pathlist = MyStringUtil.splitstr(path,"/");
        Object res = filesystem.mkdir(fileSystem, pathlist,"");
        return res;
    }

    public Object listdir(String fileSystem,String path){
        List<String> pathlist = MyStringUtil.splitstr(path,"/");
        return filesystem.listdir(fileSystem, pathlist);
    }

    public Object delete(String fileSystem,String path){
        List<String> pathlist = MyStringUtil.splitstr(path,"/");
        Object res = filesystem.delete(fileSystem, pathlist);
        return res;
    }

    public Object open(String fileSystem,String path){
        List<String> pathlist = MyStringUtil.splitstr(path,"/");
        Object res = filesystem.open(fileSystem, pathlist);
        return res;
    }

    public Object commitWrite(String fileSystem,String path,Integer size){
        List<String> pathlist = MyStringUtil.splitstr(path,"/");
        Object res = filesystem.commitwrite(fileSystem, pathlist, size);
        return res;
    }
}
