package com.distributed.uid.manager.impl;

import com.distributed.uid.manager.ISequenceManager;
import com.distributed.uid.util.IdWorkerSeqDO;
import com.distributed.uid.util.IdWorkerSeqMapper;
import com.distributed.uid.util.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @author typhoon
 * @date 2022-11-17 14:3 Thursday
 **/
@Slf4j
public class DbSequenceManager implements ISequenceManager<Long> {


    @Autowired(required = false)
    protected IdWorkerSeqMapper idWorkerSeqMapper;

    @Override
    public long borrowSeq(Long seq) {
        if (null == idWorkerSeqMapper) {
            throw new RuntimeException("您选择了DB作为中心化节点,但是缺少DB持久层实现");
        }
        String hostIp = NetUtil.getHostIp();
        IdWorkerSeqDO seqDO = new IdWorkerSeqDO();
        seqDO.setHostId(hostIp);
        seqDO.setCreateTime(new Date());
        idWorkerSeqMapper.insert(seqDO);
        log.info("DbSequenceManager.borrowSeq success,seqId={}", seqDO.getId());
        return seqDO.getId();
    }

    @Override
    public void returnSeq(Long seq) {
        log.info("DbSequenceManager.returnSeq,seqId={}", seq);
        if (null == idWorkerSeqMapper) {
            throw new RuntimeException("您选择了DB作为中心化节点,但是缺少DB持久层实现");
        }
        idWorkerSeqMapper.deleteByPrimaryKey(seq);
    }
}
