package com.distributed.uid.manager.impl;

import com.distributed.uid.manager.ISequenceManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;


/**
 * Zk序列管理器
 *
 *
 * @author typhoon
 **/
@Slf4j
public class ZkSequenceManager implements ISequenceManager<String> {


    private String path;

    protected CuratorFramework curatorFramework;

    public ZkSequenceManager(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
    }

    @Override
    public long borrowSeq(String seqKey) {
        if (null == curatorFramework) {
            throw new RuntimeException("雪花算法选择了zookeeper作为中心化节点,但是缺少zookeeper相关配置");
        }
        //this.curatorFramework.start();
        String tempPath;
        try {
            tempPath = this.curatorFramework.create().
                    creatingParentsIfNeeded().
                    withMode(CreateMode.EPHEMERAL_SEQUENTIAL).
                    forPath(seqKey);
        } catch (Exception e) {
            log.error("ZkSequenceManager.borrowSeq create ephemeral path occur error;seqKey={}",seqKey,e);
            throw new RuntimeException("雪花算法选择了zookeeper作为中心化节点,但是创建临时路径失败",e);
        }

        this.path = tempPath;

        long seq = Long.valueOf(tempPath.substring(tempPath.lastIndexOf("-") + 1));

        log.info("ZkSequenceManager.borrowSeq success,seqId={}", seq);
        return seq;
    }

    @Override
    public void returnSeq(String seqKey) {
        log.info("ZkSequenceManager.returnSeq,seqKey={}", seqKey);
        try {
            if(null != this.curatorFramework) {
                this.curatorFramework.close();
            }
        } catch (Exception e) {
            log.error("ZkSequenceManager.returnSeq close curator occur error;seqKey={}",seqKey,this.curatorFramework);
        }
    }
}
