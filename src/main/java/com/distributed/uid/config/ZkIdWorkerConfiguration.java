package com.distributed.uid.config;

import com.distributed.uid.idworker.ZkIdWorker;
import com.distributed.uid.manager.impl.ZkSequenceManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

/**
 * zk中心化发号器配置
 *
 * @author typhoon
 **/
@Slf4j
public class ZkIdWorkerConfiguration {

    @Value("${spring.application.name:}")
    private String applicationName;


    @Bean("zkSequenceManager")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public ZkSequenceManager zkSequenceManager(CuratorFramework curatorFramework) {
        ZkSequenceManager zkSequenceManager = new ZkSequenceManager(curatorFramework);
        log.info("ZkIdWorkerConfiguration.redisSeqManager init success......");
        return zkSequenceManager;
    }


    @Bean("zkIdWorker")
    public ZkIdWorker zkIdWorker(@Qualifier("zkSequenceManager") ZkSequenceManager zkSequenceManager) {
        ZkIdWorker idWorker = new ZkIdWorker(zkSequenceManager);
        idWorker.setSeqKey("/" + applicationName + idWorker.getSeqKey());
        log.info("ZkIdWorkerConfiguration.redisIdWorker init success....");
        return idWorker;
    }
}
