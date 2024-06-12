package com.distributed.uid.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({CuratorFramework.class})
@ConditionalOnProperty(value = "zookeeper.address")
public class ZkConfig {

    @Value("${zookeeper.address}")
    private String zkAddress;


    @Bean
    @ConditionalOnMissingBean(CuratorFramework.class)
    public CuratorFramework curatorFramework() {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().
                connectString(this.zkAddress).  // 若配置集群则是 ip1:port, ip2:post, ip3:port(,隔开)
                sessionTimeoutMs(5000).  // 超时，也是心跳时间
                retryPolicy(new ExponentialBackoffRetry(1000, 3)). // 重试策略，3次且每次多1s
                build();
        //启动交给使用者
        curatorFramework.start();
        return curatorFramework;
    }
}
