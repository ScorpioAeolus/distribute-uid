package com.distributed.uid.manager;

/**
 * @author typhoon
 * @date 2022-11-17 14:3 Thursday
 **/
public abstract class AbstractRedisSequenceManager implements ISequenceManager<String> {

    protected static final String LUA_BORROW_SCRIPT = "local seq = redis.call('incr',KEYS[1]);" +
            "redis.call('expire',KEYS[1],ARGV[1]);" +
            "return seq;";

    protected static final String LUA_RETURN_SCRIPT = "redis.call('expire',KEYS[1],1);";
}
