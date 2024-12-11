package com.distributed.uid.config;

import com.distributed.uid.idworker.DBIdWorker;
import com.distributed.uid.manager.impl.DbSequenceManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * db中心化发号器配置
 *
 * @author typhoon
 **/
@Slf4j
public class DBIdWorkerConfig {

    @Bean("dbSequenceManager")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public DbSequenceManager dbSequenceManager() {
        DbSequenceManager dbSequenceManager = new DbSequenceManager();
        log.info("RedisIdWorkerConfig.dbSeqManager init success......");
        return dbSequenceManager;
    }

    @Bean("dbIdWorker")
    public DBIdWorker dbIdWorker(@Qualifier("dbSequenceManager") DbSequenceManager dbSequenceManager) {
        DBIdWorker idWorker = new DBIdWorker(dbSequenceManager);
        log.info("DBIdWorkerConfig.dbIdWorker init success....");
        return idWorker;
    }

    @Bean
    @ConditionalOnBean(DataSource.class)
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        log.info("DBIdWorkerConfig jdbcTemplate init success");
        return jdbcTemplate;
    }
}
