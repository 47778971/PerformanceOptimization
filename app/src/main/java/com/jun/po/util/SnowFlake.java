package com.jun.po.util;

public class SnowFlake {
    /**
     * 开始时间截
     */
    private final long startTimeStamp = 1420041600000L;
    /**
     * 机器id所占的位数
     */
    private final long workerIdBits = 5L;
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
     * 序列在id中占的位数
     */
    private final long sequenceBits = 12L;
    /**
     * 机器ID向左移12位
     */
    private final long workerIdShift = sequenceBits;
    /**
     * 数据标识id向左移17位(12+5)
     */
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    /**
     * 时间截向左移22位(5+5+12)
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;
    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
     */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);
    /**
     * 工作机器ID(0~31)
     */
    private long workerId;
    /**
     * 数据中心ID(0~31)
     */
    private long dataCenterId;
    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence = 0L;
    /**
     * 上次生成ID的时间截
     */
    private long lastTimeStamp = -1L;

    /**
     * @param workerId     工作ID (0~31)
     * @param dataCenterId 数据中心ID (0~31)
     */
    public SnowFlake(long workerId, long dataCenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (this.dataCenterId > maxDataCenterId || this.dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("dataCenter Id can't be greater than %d or less than 0", maxDataCenterId));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimeStamp) {//如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimeStamp - timestamp));
        }
        if (lastTimeStamp == timestamp) {//如果是同一时间生成的，则进行毫秒内序列
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {//毫秒内序列溢出
                timestamp = tilNextMillis(lastTimeStamp);//阻塞到下一个毫秒,获得新的时间戳
            }
        } else {//时间戳改变，毫秒内序列重置
            sequence = 0L;
        }
        lastTimeStamp = timestamp;//上次生成ID的时间截
        return ((timestamp - startTimeStamp) << timestampLeftShift) //
                | (dataCenterId << datacenterIdShift) //
                | (workerId << workerIdShift) //
                | sequence;//移位并通过或运算拼到一起组成64位的ID
    }
}