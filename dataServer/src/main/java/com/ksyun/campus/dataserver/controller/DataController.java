package com.ksyun.campus.dataserver.controller;

import com.ksyun.campus.dataserver.dto.BlockData;
import com.ksyun.campus.dataserver.services.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController("/")
public class DataController {
    /**
     * 1、读取request content内容并保存在本地磁盘下的文件内
     * 2、同步调用其他ds服务的write，完成另外2副本的写入
     * 3、返回写成功的结果及三副本的位置
     * @param fileSystem
     * @param path
     * @param offset
     * @param length
     * @return
     */
    @Autowired
    private DataService dataservice;

    /**
     * 在指定本地磁盘路径下，读取指定大小的内容后返回
     * @param blockid
     * @param length
     * @return
     */
    @RequestMapping("read")
    public Object readFile(@RequestParam String blockid, @RequestParam Integer length){
        return dataservice.read(blockid,length);
    }
    /**
     * 在指定本地磁盘路径下写文件
     * @return
     */
    @RequestMapping("write")
    public Object writeFile(HttpServletRequest request, @RequestBody BlockData body){
        return dataservice.write(body);
    }
    /**
     * 关闭退出进程
     */
    @RequestMapping("shutdown")
    public void shutdownServer(){
        System.exit(-1);
    }

    @RequestMapping("test")
    public String test(HttpServletRequest request, @RequestBody BlockData body)
    {
        return "123";
    }
}
