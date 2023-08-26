package com.ksyun.campus.metaserver.util;

import com.ksyun.campus.metaserver.domain.FileType;
import com.ksyun.campus.metaserver.domain.ReplicaData;
import com.ksyun.campus.metaserver.domain.StatInfo;
import com.ksyun.campus.metaserver.dto.BlockData;
import com.ksyun.campus.metaserver.dto.DataServerMsg;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Data
@Slf4j
public class FileSystem {
    List<FileSystem> fileDirTrees;
    StatInfo statinfo;
    String filename;
    String fileSystem;
    private ZKUtil zkutil;
    public FileSystem(){
        fileDirTrees = new ArrayList<>();
        statinfo = new StatInfo();
        statinfo.setType(FileType.Directory);
        statinfo.setPath("/");
        zkutil = new ZKUtil();
    }
    public FileSystem(String fileSystem, FileType filetype,String fileName, long size){
        statinfo = new StatInfo();
        statinfo.setType(filetype);
        statinfo.setSize(size);
        statinfo.setMtime(Instant.now().getEpochSecond());//获取当前的unix时间戳
        zkutil = new ZKUtil();
        fileDirTrees = new ArrayList<>();
        this.filename = fileName;
        this.fileSystem = fileSystem;
    }
    public boolean create(String fileSystem, List<String> pathlist, String path, String pathstat){
        FileSystem cur = null;
        if(pathlist.size()==1){//目录的最后一项已经是文件了
            for(FileSystem fs :fileDirTrees){
                if(fs.getFilename().equals(pathlist.get(0)) && fs.getFileSystem().equals(fileSystem) && fs.statinfo.getType() == FileType.File){
                    cur = fs;break;
                }
            }
            if(cur==null) {
                log.info("文件不存在");
                cur = new FileSystem(fileSystem, FileType.File, pathlist.get(0),0);
                List<ReplicaData> dataserverlist = zkutil.getDataServerList();
                for (ReplicaData re : dataserverlist) {
                    re.setId(UUID.randomUUID().toString());//设置Blockid
                    re.setPath(path);
                }
                cur.getStatinfo().setPath(path);
                cur.getStatinfo().setReplicaData(dataserverlist);
                fileDirTrees.add(cur);
                return true;
            }
            else{
                log.error("文件创建失败，该文件已经存在");
                return false;
            }
        }
        else{//目录的中间项目
            for(FileSystem fs :fileDirTrees){
                if(fs.getFilename().equals(pathlist.get(0)) && fs.getFileSystem().equals(fileSystem) && fs.statinfo.getType() == FileType.Directory){
                    cur = fs;break;
                }
            }
            if(cur==null){//目录不存在
                cur = new FileSystem(fileSystem,FileType.Directory,pathlist.get(0), 0);
                pathstat += "/"+pathlist.get(0);
                cur.getStatinfo().setPath(pathstat);
                fileDirTrees.add(cur);
                pathlist.remove(0);
                return cur.create(fileSystem, pathlist, path, pathstat);
            }else{//目标存在
                pathstat += "/"+pathlist.get(0);
                pathlist.remove(0);
                return cur.create(fileSystem, pathlist, path, pathstat);
            }
        }
    }

    public boolean commitwrite(String fileSystem, List<String> pathlist, Integer size){
        FileSystem cur = null;
        if(pathlist.size()==1){//目录的最后一项已经是文件了
            for(FileSystem fs :fileDirTrees){
                if(fs.getFilename().equals(pathlist.get(0)) && fs.getFileSystem().equals(fileSystem) && fs.statinfo.getType() == FileType.File){
                    cur = fs;break;
                }
            }
            if(cur==null) {
                log.info("文件不存在");
                return false;
            }
            else{
                cur.getStatinfo().setSize(size);
                return true;
            }
        }
        else{//目录的中间项目
            for(FileSystem fs :fileDirTrees){
                if(fs.getFilename().equals(pathlist.get(0)) && fs.getFileSystem().equals(fileSystem) && fs.statinfo.getType() == FileType.Directory){
                    cur = fs;break;
                }
            }
            if(cur==null){//目录不存在
                return false;
            }else{//目标存在
                pathlist.remove(0);
                return cur.commitwrite(fileSystem, pathlist,size);
            }
        }
    }
    public List<ReplicaData> open(String fileSystem, List<String> pathlist){
        FileSystem cur = null;
        if(pathlist.size()==1){//目录的最后一项已经是文件了
            for(FileSystem fs :fileDirTrees){
                if(fs.getFilename().equals(pathlist.get(0)) && fs.getFileSystem().equals(fileSystem) && fs.statinfo.getType() == FileType.File){
                    cur = fs;break;
                }
            }
            if(cur==null){
                log.error("文件不存在");
                return null;
            }else{
                log.info("文件打开成功");
                return cur.getStatinfo().getReplicaData();
            }
        }
        else{//目录的中间项目
            for(FileSystem fs :fileDirTrees){
                if(fs.getFilename().equals(pathlist.get(0)) && fs.getFileSystem().equals(fileSystem) && fs.statinfo.getType() == FileType.Directory){
                    cur = fs;break;
                }
            }
            if(cur==null){//目录不存在
                return null;
            }else{//目标存在
                pathlist.remove(0);
                return cur.open(fileSystem, pathlist);
            }
        }
    }
    public Boolean mkdir(String fileSystem, List<String> pathlist, String pathstat){
        if(pathlist.size()==0)return true;
        FileSystem cur = null;
        for(FileSystem fs :fileDirTrees){
            if(fs.getFilename().equals(pathlist.get(0)) && fs.getFileSystem().equals(fileSystem) && fs.getStatinfo().getType() == FileType.Directory){
                cur = fs;break;
            }
        }
        if(cur==null){//目录不存在
            cur = new FileSystem(fileSystem,FileType.Directory,pathlist.get(0),0);
            pathstat += "/"+pathlist.get(0);
            cur.getStatinfo().setPath(pathstat);
            fileDirTrees.add(cur);
            pathlist.remove(0);
            return cur.mkdir(fileSystem,pathlist,pathstat);
        }else{//目标存在
            if(pathlist.size()==1)return false;//最后一个节点存在则目录创建失败，因为已经存在了。
            pathstat += "/"+pathlist.get(0);
            pathlist.remove(0);
            return cur.mkdir(fileSystem,pathlist,pathstat);
        }
    }

    public StatInfo stats(String fileSystem, List<String> pathlist){
        FileSystem cur = null;
        if(pathlist.size()==1){
            for(FileSystem fs :fileDirTrees){
                if(fs.getFilename().equals(pathlist.get(0)) && fs.getFileSystem().equals(fileSystem)){
                    cur = fs;break;
                }
            }
            if(cur==null){
                log.info("节点信息查询失败，节点不存在1");
                return null;
            }else{
                StatInfo res = cur.getStatinfo();
                return res;
            }
        }else{
            for(FileSystem fs :fileDirTrees){
                if(fs.getFilename().equals(pathlist.get(0)) && fs.getFileSystem().equals(fileSystem) && fs.getStatinfo().getType() == FileType.Directory){
                    cur = fs;break;
                }
            }
            if(cur==null){
                log.info("节点信息查询失败，节点不存在2");
                return null;
            }else{
                pathlist.remove(0);
                return cur.stats(fileSystem, pathlist);
            }
        }
    }


    public List<StatInfo> listdir(String fileSystem, List<String> pathlist){
        FileSystem cur = null;
        List<StatInfo> res = new ArrayList<>();
        if(pathlist.size()==0){
            for(FileSystem fs :fileDirTrees){
                    res.add(fs.getStatinfo());
                }
            return res;
        }else{
            for(FileSystem fs :fileDirTrees){
                if(fs.getFilename().equals(pathlist.get(0)) && fs.getFileSystem().equals(fileSystem) && fs.getStatinfo().getType() == FileType.Directory){
                    cur = fs;break;
                }
            }
            if(cur == null){
                return res;
            }else{
                pathlist.remove(0);
                return cur.listdir(fileSystem,pathlist);
            }

        }
    }

    public Boolean delete(String fileSystem, List<String> pathlist){
        boolean ifremove = false;
        FileSystem cur = null;
        if(pathlist.size()==1){
            for(FileSystem fs :fileDirTrees){
                if(fs.getFilename().equals(pathlist.get(0)) && fs.getFileSystem().equals(fileSystem)){
                    fileDirTrees.remove(fs);
                    ifremove = true;
                    break;
                }
            }
            return ifremove;
        }else{
            for(FileSystem fs :fileDirTrees){
                if(fs.getFilename().equals(pathlist.get(0)) && fs.getFileSystem().equals(fileSystem) && fs.getStatinfo().getType() == FileType.Directory){
                    cur=fs;break;
                }
            }
            if(cur==null){
                return ifremove;
            }else{
                pathlist.remove(0);
                return cur.delete(fileSystem,pathlist);
            }
        }
    }
}