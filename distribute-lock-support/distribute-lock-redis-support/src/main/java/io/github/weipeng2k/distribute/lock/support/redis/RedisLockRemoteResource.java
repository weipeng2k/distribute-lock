package io.github.weipeng2k.distribute.lock.support.redis;

import io.github.weipeng2k.distribute.lock.spi.AcquireResult;
import io.github.weipeng2k.distribute.lock.spi.LockRemoteResource;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * 基于Redis的锁实现
 * </pre>
 *
 * @author weipeng2k 2021年11月12日 下午22:53:20
 */
public class RedisLockRemoteResource implements LockRemoteResource {

    private RedisCommands<String, String> syncCommands;

    private int minSpinMillis;

    private int randomMillis;

    public RedisLockRemoteResource(String host, int port, String randomSpinMillisIntervals) {

    }

    @Override
    public AcquireResult tryAcquire(String resourceName, String resourceValue, long waitTime, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public void release(String resourceName, String resourceValue) {

    }
}
