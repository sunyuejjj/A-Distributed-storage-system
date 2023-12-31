# 仿照HDFS实现了一个简易的分布式文件存储系统
项目分为Client，DataNode，NameNode三部分，使用
Zookeeper作为服务注册与发现中心，并提供自动故障转移工作。
## 项目要点：
1. Client实现：实现了HDFS中的FileSystem对象，对外提供open、mkdir、delete等文件操作，使用Http协议实现上述
   文件操作功能；
2. NameNode实现：实现主从节点多实例部署，采用目录树结构存储元数据信息，并对外开放文件操作接口。在zk中进行
   注册并监听zk中DataNode的容量信息，实现多副本写均衡调度。采用多线程定时任务实现fsck和recovery，保证DataNode
   中三副本的一致性和可用性；
3. DataNode实现：实现多实例部署，采用三副本写机制，将数据写入磁盘进行持久化，通过CRC校验的方式，在读取和
   写入数据时进行数据校验，保证数据完整性。在zk中注册实例信息，定期上报zk当前节点的容量信息； 
## 应用技术
Java、Zookeeper