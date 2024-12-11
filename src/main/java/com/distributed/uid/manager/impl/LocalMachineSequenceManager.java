package com.distributed.uid.manager.impl;

import com.distributed.uid.config.CustomLocalMachinePathConfig;
import com.distributed.uid.manager.ISequenceManager;
import com.distributed.uid.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author typhoon
 * @date 2024/6/12 2:31 下午
 **/
@Slf4j
public class LocalMachineSequenceManager implements ISequenceManager<String> {

    @Resource
    CustomLocalMachinePathConfig customLocalMachinePathConfig;

    private static final String SEQ_SUFFIX = "_seq";

    @Override
    public long borrowSeq(String applicationName) {
        Long seq = this.customLocalMachinePathConfig.getSeq();
        if(null != seq) {
            log.info("LocalMachineSequenceManager.borrowSeq use custom seq config;applicationName={},seq={}",applicationName,seq);
            return seq;
        }
        String props = applicationName.replaceAll("-","_") + SEQ_SUFFIX;
        String seq_val = System.getenv(props);
        if(null == seq_val || !StringUtil.isNumeric(seq_val)) {
            throw new RuntimeException("您选择了本地机器变量作为分布式uid序列号,但是缺少变量配置或者配置非法");
        }
        return Long.parseLong(seq_val);
    }

    @Override
    public void returnSeq(String applicationName) {
        log.info("LocalMachineSequenceManager.returnSeq ignore...");
    }
}
