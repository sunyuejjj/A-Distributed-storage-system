package com.ksyun.campus.metaserver.controller;

import com.ksyun.campus.metaserver.services.MetaService;
import com.ksyun.campus.metaserver.util.FileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class MetaController {
    @Autowired
    private MetaService metaservice;
    @RequestMapping("stats")
    public Object stats(@RequestParam String fileSystem, @RequestParam String path){
        return metaservice.stats(fileSystem, path);
    }
    @RequestMapping("create")
    public Object createFile(@RequestParam String fileSystem, @RequestParam String path){
        return metaservice.createFile(fileSystem, path);
    }
    @RequestMapping("mkdir")
    public Object mkdir(@RequestParam String fileSystem, @RequestParam String path){
        return metaservice.mkdir(fileSystem, path);
    }
    @RequestMapping("listdir")
    public Object listdir(@RequestParam String fileSystem,@RequestParam String path){
        return metaservice.listdir(fileSystem, path);
    }
    @RequestMapping("delete")
    public Object delete(@RequestParam String fileSystem, @RequestParam String path){
        return metaservice.delete(fileSystem, path);
    }

    /**
     * 保存文件写入成功后的元数据信息，包括文件path、size、三副本信息等
     * @param fileSystem
     * @param path
     * @return
     */
    @RequestMapping("write")
    public Object commitWrite(@RequestParam String fileSystem, @RequestParam String path, @RequestParam Integer size){
        return metaservice.commitWrite(fileSystem, path, size);
    }

    /**
     * 根据文件path查询三副本的位置，返回客户端具体ds、文件分块信息
     * @param fileSystem
     * @param path
     * @return
     */
    @RequestMapping("open")
    public Object open(@RequestParam String fileSystem,@RequestParam String path){
        return metaservice.open(fileSystem, path);
    }

    /**
     * 关闭退出进程
     */
    @RequestMapping("shutdown")
    public void shutdownServer(){
        System.exit(-1);
    }

}
