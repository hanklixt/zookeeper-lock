---
title: zookeeper 分布式锁相关知识点
tags: zookeeper,lock
renderNumberedHeading: true
grammar_cjkRuby: true
---

#  基本命令使用(zookeeper3.4.10)
  -   ./zkCli.sh     连接zk服务端
  -   ls  /               列出根节点下的子节点
  -   create  /apps   "hello app"   创建节点apps,并存入字符串
  -   get /apps     获取该节点详细信息
      [zk: localhost:2181(CONNECTED) 14] get /apps   
      this is app node
      cZxid = 0x2       //节点事务id
      ctime = Wed Jan 15 11:02:59 CST 2020     //节点创建时间
      mZxid = 0x2       //更新节点的事务id
      mtime = Wed Jan 15 11:02:59 CST 2020   //更新时间
      pZxid = 0x2         //父节点id   
      cversion = 0       //创建的版本
      dataVersion = 0     //数据版本,从0开始计数
      aclVersion = 0       //权限版本
      ephemeralOwner = 0x0    //是否是临时节点，如是0，则不是临时节点(16进制)
      dataLength = 16
      numChildren = 0     //子节点个数
  -   ls /apps       列出节点apps下的子节点
  -    set path data [version]     更新节点数据
 #  节点类型

-   **类型描述**
    | 类型        | 描述    | 
    | --------   | -----:   |
    | PERSISTENT        | 持久节点      |  
    | PERSISTENT_SEQUENTIAN        | 持久序号节点      |   
    | EPHEMERAL        | 临时节点(不可再拥有子节点)      |   
	| EPHEMERAL_SEQUENTIAN        | 临时序号节点(不可在拥有子节点)      |   

-  1.持久节点(PERSISTENT)
   持久化保存的节点，也是默认创建的
``` javascript
       # 默认创建的节点就是持久节点
       create /test
```
-   2.持久序号节点
  持久化序号节点加-s参数
   

``` javascript
      # 创建持久序号节点并设置存储数据为12
      create -s /tmp 12
```

 -  3.临时节点
  临时节点加-e参数
``` javascript
    #创建临时节点设置数据为11
    create -e /test  11
```
-  4.临时顺序节点
   临时顺序节点加 -s  -e 参数
   
``` javascript
  #创建临时顺序节点并设值为141
   create -s -e /test 141
```
 # 实现介绍
-   共享锁:也称为只读锁，当一方获取到资源后，其他方也可以获得共享锁，但其只允许读取，在共享锁全部释放之前。其他方不能获取写锁(未实现)
-   排他锁，也称为读写锁，获得排他锁后，可以进行数据的读写，在其释放之前，其他方不能获得任何锁
# 实现原理
   1.创建zk客户端
   2.创建临时节点
   3.取得根节点下所有临时节点，如果创建的当前节点前面没有节点，则获取锁
   4.如果还有临时节点，尝试重新获取锁，并设置上一个节点的监听。
   5.如果上个临时节点被删除，再次尝试重新获取锁。

  
    

 

   





     