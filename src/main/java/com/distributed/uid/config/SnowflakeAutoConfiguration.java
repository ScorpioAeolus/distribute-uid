package com.distributed.uid.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 发号器自动配置
 *
 * @author typhoon
 */
@Configuration
@Import({RedisConfig.class})
public class SnowflakeAutoConfiguration {


}
