package com.distributed.uid.manager;


/**
 * @author typhoon
 * @@date 2022-11-17 14:3 Thursday
 **/
public interface ISequenceManager<T> {

    long borrowSeq(T t);

    void returnSeq(T t);

}
