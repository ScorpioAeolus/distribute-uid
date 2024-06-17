package com.distributed.uid.manager;


/**
 * 序列管理器接口
 *
 * @author typhoon
 **/
public interface ISequenceManager<T> {

    long borrowSeq(T t);

    void returnSeq(T t);

}
