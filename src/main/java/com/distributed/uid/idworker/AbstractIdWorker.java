package com.distributed.uid.idworker;

/**
 * 基于雪花算法的抽象发号器
 *
 * @author typhoon
 * @date 2022-11-17 14:3 Thursday
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * Twitter_Snowflake<br>
 * SnowFlake的结构如下(每部分用-分开):<br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
 * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0<br>
 * 41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截)
 * 得到的值），这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的（如下下面程序IdWorker类的startTime属性）。41位的时间截，可以使用69年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69<br>
 * 10位的数据机器位，可以部署在1024个节点，包括5位dataCenterId和5位workerId<br>
 * 12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号<br>
 * 加起来刚好64位，为一个Long型。<br>
 * SnowFlake的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)，并且效率较高，经测试，SnowFlake每秒能够产生26万ID左右。
 */
@Slf4j
public abstract class AbstractIdWorker implements InitializingBean, DisposableBean {

    /**
     * 起始时间戳(2020-01-01)，用于用当前时间戳减去这个时间戳，算出偏移量
     **/
    private final long startTime = 1577808000000L;

    /**
     * 机器id所占的位数
     */
    private final long workerIdBits = 5L;

    protected final long workerIdCount = 1 << workerIdBits;

    /**
     * 数据标识id所占的位数
     */
    private final long dataCenterIdBits = 5L;

    /**
     * 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /**
     * 支持的最大数据标识id，结果是31
     */
    private final long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);

    /**
     * 序列号在id中占的位数
     */
    private final long sequenceBits = 8L;

    /**
     * 机器ID向左移12位
     */
    private final long workerIdShift = sequenceBits;

    /**
     * 数据标识id向左移17位(12+5)
     */
    private final long dataCenterIdShift = sequenceBits + workerIdBits;

    /**
     * 时间截向左移22位(5+5+12)
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;

    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
     */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);
    /**
     * 数据中心ID(0~31)
     */
    private final long dataCenterId;
    /**
     * 工作机器ID(0~31)
     */
    private long workerId;
    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence = 0L;

    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;

    public AbstractIdWorker(long distributedId) {
        distributedId = distributedId % 1024;
        String distributedIdStr = Long.toBinaryString(distributedId);
        if (distributedIdStr.length() > 5) {
            workerId = Long.parseLong(distributedIdStr.substring(distributedIdStr.length() - 5), 2);
            dataCenterId = Long.parseLong(distributedIdStr.substring(0, distributedIdStr.length() - 5), 2);
        } else {
            workerId = distributedId;
            dataCenterId = 0;
        }
    }

    /**
     * 构造函数
     *
     * @param workerId     工作机器ID,数据范围为0~31
     * @param dataCenterId 数据中心ID,数据范围为0~31
     */
    public AbstractIdWorker(long workerId, long dataCenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("workerId can't be greater than %d or less than 0", maxWorkerId));
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("dataCenterId can't be greater than %d or less than 0", maxDataCenterId));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    /**
     * HostAddress的信息作为机器ID
     * HostName的信息作为数据中心ID
     */
    public AbstractIdWorker() {
        //this.workerId = getWorkId();
        this.dataCenterId = getCenterId();
        log.info("SnowflakeIdWorker init success！workerId:{}, dataCenterId:{}", workerId, dataCenterId);
    }

    /**
     * 获取字节数组，相加取余
     *
     * @param s
     * @param max
     * @return
     */
    protected static long getHostId(String s, int max) {
        byte[] bytes = s.getBytes();
        long sum = 0;
        for (int b : bytes) sum += b;
        return sum % (max + 1);
    }

    /**
     * 获取数据中心ID
     *
     * @return
     */
    public static long getCenterId() {
        try {
            return getHostId(Inet4Address.getLocalHost().getHostName(), 31);
        } catch (UnknownHostException e) {
            int centerId = new Random().nextInt(32);
            log.info("GetHostName fail, Random centerId:{}", centerId);
            return centerId;
        }
    }

    /**
     * 获取中心化机器ID
     *
     * @return
     */
    protected abstract long getWorkerId();

    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;

        /**
         * 移位并通过或运算拼到一起组成64位的ID
         * 1.左移运算是为了将数值移动到对应的段(41、5、5，12那段因为本来就在最右，因此不用左移)
         * 2.然后对每个左移后的值(la、lb、lc、sequence)做位或运算，是为了把各个短的数据合并起来，合并成一个二进制数
         * 3.最后转换成10进制，就是最终生成的id
         */
        return ((timestamp - startTime) << timestampLeftShift) //
                | (dataCenterId << dataCenterIdShift) //
                | (workerId << workerIdShift) //
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.workerId = this.getWorkerId();
        log.info("AbstractIdWorker.afterPropertiesSet init workerId success;workerId={}", this.workerId);
    }

    @Override
    public void destroy() throws Exception {
        this.doDestroyWorkerId();
    }

    /**
     * 应用关闭时,回收中心化
     */
    protected abstract void doDestroyWorkerId();
}
