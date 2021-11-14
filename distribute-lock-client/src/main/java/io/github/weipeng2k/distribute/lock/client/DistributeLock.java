package io.github.weipeng2k.distribute.lock.client;

import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * 分布式的锁
 * 对于锁，需要兑现两个含义：
 * <li>可见性</li>
 * <li>排他性</li>
 *
 * </pre>
 *
 * @author weipeng2k 2021年11月07日 下午17:53:29
 */
public interface DistributeLock {

    /**
     * <pre>
     * 尝试等待waitTime的时间进行锁的获取，如果时间范围内无法获取到则返回false
     *
     * </pre>
     *
     * @param waitTime 获取锁的最大等待超时时间
     * @param unit     时间单位
     * @return 是否锁定成功，如果返回false表示锁定失败
     */
    boolean tryLock(long waitTime, TimeUnit unit) throws InterruptedException;

    /**
     * 解锁，释放锁资源
     */
    void unlock();
}
