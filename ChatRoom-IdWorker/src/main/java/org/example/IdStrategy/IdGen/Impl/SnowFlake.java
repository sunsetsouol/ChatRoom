package org.example.IdStrategy.IdGen.Impl;

import org.example.IdStrategy.IdGen.IdGenerator;
import org.example.IdStrategy.IdType.IdType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/2/15
 */
@Component
public class SnowFlake extends IdGenerator {

    /**
     * 开始时间戳
     */
    public final long START_STAMP = 1708000000000L;

//    /**
//     * 时间戳位数
//     */
//    @Value("${id-worker.time-bit:41L}")
//    public long timeBit;

    /**
     * 数据中心位数
     */
    @Value("${id-worker.data-center-bit:5}")
    public long dataCenterBit;

    /**
     * 机器位数
     */
    @Value("${id-worker.worker-bit:5}")
    public long workerBit;

    /**
     * 序列号位数
     */
    @Value("${id-worker.sequence-bit:12}")
    public long sequenceBit;


    /**
     * 数据中心id
     */
    @Value("${id-worker.data-center-id:1}")
    public long dataCenterId;

    /**
     * 机器id
     */
    @Value("${id-worker.worker-id:1}")
    public long workerId;

    public long timeStampShift = dataCenterBit + workerBit + sequenceBit;

    public long dataCenterShift = workerBit + sequenceBit;

    public long workerShift = sequenceBit;


    /**
     * 序列号
     */
    public final AtomicLong sequence = new AtomicLong(0);

    /**
     * 序列号最大值
     */
    public long sequenceMask = ~(-1L << sequenceBit);

    /**
     * 上一个时间戳
     */
    public long lastTimeStamp = -1L;

    @PostConstruct
    public void check() {
        if (dataCenterId > ((~(-1L << sequenceBit + workerBit)))) {
            throw new RuntimeException("dataCenterId超出范围");
        }
        if (workerId > ((~(-1L << sequenceBit)))) {
            throw new RuntimeException("workerId超出范围");
        }
        timeStampShift = dataCenterBit + workerBit + sequenceBit;
        dataCenterShift = workerBit + sequenceBit;
        workerShift = sequenceBit;
        sequenceMask = ~(-1L << sequenceBit);
    }

    @Override
    public long getLongId() {

        long now = System.currentTimeMillis();

        long mySequence = sequence.getAndIncrement();

        if (now > lastTimeStamp) {
            //超过上一个时间戳，序列号重置，加锁保证原子性
            synchronized (sequence) {
                if (now > lastTimeStamp) {
                    lastTimeStamp = now;
                    sequence.set(0);
                }
            }
            mySequence = sequence.getAndIncrement();
        } else if (now == lastTimeStamp) {
            //序列号+1，防止序列号溢出
            mySequence = (mySequence + 1) & sequenceMask;
            if (mySequence == 0) {
                //时间戳相同，序列号溢出，虽然不太可能，但是还是要有兜底方案
                now = toNextMillis(lastTimeStamp);
                mySequence=sequence.getAndIncrement();
            }
        } else {
            //时钟回拨
            long offset = lastTimeStamp - now;
            if (offset > 100) {
                //如果时钟回拨过大则抛出异常
                throw new RuntimeException("时钟回拨异常");
            } else {
                //时间相差不大阻塞一下
                try {
                    wait(2 * offset);
                    now = System.currentTimeMillis();
                    if (now < lastTimeStamp) {
                        //如果还是小于上一个时间戳，抛出异常
                        throw new RuntimeException("时钟回拨异常");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        //重置上一个时间戳
        lastTimeStamp = Math.max(now, lastTimeStamp);
        return ((now - START_STAMP) << timeStampShift)
                | (dataCenterId << (dataCenterShift))
                | (workerId << workerShift)
                | mySequence;
    }

    @Override
    public String getType() {
        return IdType.LONG.type;
    }

    /**
     * 获得下一个时间戳
     *
     * @param lastTimeStamp 上一个时间戳
     * @return 下一个时间戳
     */
    public long toNextMillis(long lastTimeStamp) {
        long now = System.currentTimeMillis();
        while (now <= lastTimeStamp) {
            now = System.currentTimeMillis();
        }
        return now;
    }
}
