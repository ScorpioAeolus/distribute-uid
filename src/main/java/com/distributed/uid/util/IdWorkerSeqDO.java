package com.distributed.uid.util;

import lombok.Data;

import java.util.Date;

/**
 * 雪花算法序列表
 */
@Data
public class IdWorkerSeqDO {

    private Long id;

    private String hostId;

    private Date createTime;

}