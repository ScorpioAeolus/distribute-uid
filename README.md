
distribute-uid
========
[![Apache License 2](https://img.shields.io/badge/license-ASF2-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt) [![Build Status](https://github.com/lukas-krecan/ShedLock/workflows/CI/badge.svg)](https://github.com/lukas-krecan/ShedLock/actions)

A simple distributed UID generator that is ready to use out of the box

#### Supported Provider Types
* mysql
* redis
* zookeeper
* local

#### How to use?
##### 1.Import Dependency
```
<dependency>
    <groupId>io.github.scorpioaeolus</groupId>
    <artifactId>distributed-uid</artifactId>
    <version>${version}</version>
</dependency>
```

##### 2.Enable IdWorker capability
```java
@SpringBootApplication
@EnableIdWorker(mode=DB)
public class XxxApplication {
    public static void main(String[] args) {
        SpringApplication.run(XxxApplication.class, args);
    }
}
```

##### 3.Usage
```
@Resource
RedisIdWorker redisIdWorker;

public void test() {
    long id = redisIdWorker.nextId();
    //...
}
```

##### 4.Provider preparation

for db,you should add table structure:
```sql
CREATE TABLE `id_worker_seq` (
                                 `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                                 `host_id` varchar(32) NOT NULL DEFAULT '' COMMENT '机器ip',
                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 PRIMARY KEY (`id`),
                                 KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=0 COMMENT='雪花算法序列表';

```
and you should also expose the bean of the Data Source.

for redis,you should expose the bean of the RedisConnectionFactory.
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

for zookeeper,you should expose the bean of the CuratorFramework:
```
<dependency>
  <groupId>org.apache.curator</groupId>
  <artifactId>curator-framework</artifactId>
</dependency>
```
