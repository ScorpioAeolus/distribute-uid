CREATE TABLE `id_worker_seq` (
                                 `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                                 `host_id` varchar(32) NOT NULL DEFAULT '' COMMENT '机器ip',
                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 PRIMARY KEY (`id`),
                                 KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=30001 COMMENT='雪花算法序列表';