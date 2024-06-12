package com.distributed.uid.config;

import com.distributed.uid.idworker.RedisIdWorker;
import com.distributed.uid.manager.impl.RedisSequenceManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * redis中心化发号器配置
 *
 * @author typhoon
 **/
@Slf4j
public class RedisIdWorkerConfiguration {

    @Value("${spring.application.name:}")
    private String applicationName;


    @Bean("redisSequenceManager")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public RedisSequenceManager redisSequenceManager(StringRedisTemplate stringRedisTemplate) {
        RedisSequenceManager redisSequenceManager = new RedisSequenceManager(stringRedisTemplate);
        log.info("RedisIdWorkerConfig.redisSeqManager init success......");
        return redisSequenceManager;
    }


    @Bean("redisIdWorker")
    public RedisIdWorker redisIdWorker(@Qualifier("redisSequenceManager") RedisSequenceManager redisSequenceManager) {
        RedisIdWorker idWorker = new RedisIdWorker(redisSequenceManager);
        idWorker.setSeqKey(applicationName + idWorker.getSeqKey());
        log.info("RedisIdWorkerConfig.redisIdWorker init success....");
        return idWorker;
    }
}
