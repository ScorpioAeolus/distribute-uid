package com.distributed.uid.manager.impl;

import com.distributed.uid.manager.AbstractRedisSequenceManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author typhoon
 **/
@Slf4j
public class RedisSequenceManager extends AbstractRedisSequenceManager {

    protected static final long expireTimes = 120 * 60L;
    protected RedisSerializer keySerializer = new StringRedisSerializer();
    protected RedisSerializer valueSerializer = new Jackson2JsonRedisSerializer(Object.class);
    protected StringRedisTemplate stringRedisTemplate;

    public RedisSequenceManager(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public long borrowSeq(String seqKey) {
        if (null == stringRedisTemplate) {
            throw new RuntimeException("雪花算法选择了redis作为中心化节点,但是缺少redis相关配置");
        }
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(LUA_BORROW_SCRIPT, Long.class);
        List<String> keys = new ArrayList<>(2);
        keys.add(seqKey);
        Long seq = stringRedisTemplate.execute(redisScript
                , this.valueSerializer
                , this.keySerializer
                , keys
                , expireTimes);
        log.info("RedisSequenceManager.borrowSeq success,seqId={}", seq);
        return seq;
    }

    @Override
    public void returnSeq(String seqKey) {
        log.info("RedisSequenceManager.returnSeq,seqKey={}", seqKey);
//        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>(LUA_RETURN_SCRIPT,String.class);
//        List<String> keys = new ArrayList<>(2);
//        keys.add(seqKey);
//        this.stringRedisTemplate.execute(redisScript
//                , this.valueSerializer
//                , this.keySerializer
//                , keys);
    }
}
