package com.distributed.uid;

/**
 * 中心化模式
 *
 * @author typhoon
 * @date 2022-07-12 15:26 Tuesday
 **/
public enum CenterModel {

    /**
     * db中心化
     */
    DB,
    /**
     * redis中心化
     */
    REDIS,

    /**
     * Zookeeper中心化
     */
    ZOOKEEPER,

    /**
     * Local本地化
     */
    LOCAL_MACHINE,

    ;
}
