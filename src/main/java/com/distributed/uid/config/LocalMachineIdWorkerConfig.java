package com.distributed.uid.config;

import com.distributed.uid.idworker.LocalMachineIdWorker;
import com.distributed.uid.manager.impl.LocalMachineSequenceManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

/**
 * @author typhoon
 **/
@Slf4j
public class LocalMachineIdWorkerConfig {

    @Value("${spring.application.name:}")
    private String applicationName;


    @Bean("localMachineSequenceManager")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LocalMachineSequenceManager localMachineSequenceManager() {
        LocalMachineSequenceManager localMachineSequenceManager = new LocalMachineSequenceManager();
        log.info("LocalMachineIdWorkerConfig.localMachineSequenceManager init success......");
        return localMachineSequenceManager;
    }

    @Bean("localMachineIdWorker")
    public LocalMachineIdWorker localMachineIdWorker(@Qualifier("localMachineSequenceManager") LocalMachineSequenceManager localMachineSequenceManager) {
        LocalMachineIdWorker idWorker = new LocalMachineIdWorker(localMachineSequenceManager,this.applicationName);
        log.info("LocalMachineIdWorkerConfig.localMachineIdWorker init success....");
        return idWorker;
    }


}
