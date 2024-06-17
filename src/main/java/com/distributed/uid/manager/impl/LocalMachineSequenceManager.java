package com.distributed.uid.manager.impl;

import com.distributed.uid.manager.ISequenceManager;
import com.distributed.uid.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author typhoon
 **/
@Slf4j
public class LocalMachineSequenceManager implements ISequenceManager<String> {

    private static final String SEQ_SUFFIX = "_seq";

    @Override
    public long borrowSeq(String applicationName) {
        String seq_val = System.getenv(applicationName + SEQ_SUFFIX);
        if(null == seq_val || !StringUtil.isNumeric(seq_val)) {
            throw new RuntimeException("您选择了本地机器变量作为分布式uid序列号,但是缺少变量配置或者配置非法");
        }
        return Long.valueOf(seq_val);
    }

    @Override
    public void returnSeq(String applicationName) {
        log.info("LocalMachineSequenceManager.returnSeq ignore...");
    }
}
