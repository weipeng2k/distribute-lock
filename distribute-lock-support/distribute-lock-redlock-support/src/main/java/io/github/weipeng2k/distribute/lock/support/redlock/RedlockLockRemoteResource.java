package io.github.weipeng2k.distribute.lock.support.redlock;

import io.github.weipeng2k.distribute.lock.spi.AcquireResult;
import io.github.weipeng2k.distribute.lock.spi.LockRemoteResource;
import io.github.weipeng2k.distribute.lock.spi.support.AcquireResultBuilder;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <pre>
 * 基于Redisson的 redlock 锁资源
 * </pre>
 *
 * @author weipeng2k 2021年11月14日 下午18:21:41
 */
public class RedlockLockRemoteResource implements LockRemoteResource {
    private final RedissonClient[] redissonClients;

    private final int ownSecond;

    public RedlockLockRemoteResource(String[] addresses, int ownSecond) {
        redissonClients = Arrays.stream(addresses)
                .map(address -> {
                    Config config = new Config();
                    config.useSingleServer()
                            .setAddress(address);
                    return Redisson.create(config);
                })
                .collect(Collectors.toList())
                .toArray(new RedissonClient[]{});

        this.ownSecond = ownSecond;
    }

    @Override
    public AcquireResult tryAcquire(String resourceName, String resourceValue, long waitTime,
                                    TimeUnit timeUnit) throws InterruptedException {
        RLock[] rLocks = Arrays.stream(redissonClients)
                .map(redissonClient -> redissonClient.getLock(resourceName))
                .collect(Collectors.toList())
                .toArray(new RLock[]{});
        RLock redLock = redissonClients[0].getRedLock(rLocks);

        long ownTime = timeUnit.convert(ownSecond, TimeUnit.SECONDS);
        AcquireResultBuilder acquireResultBuilder;

        boolean ret = redLock.tryLock(waitTime, ownTime, timeUnit);
        acquireResultBuilder = new AcquireResultBuilder(ret);
        if (!ret) {
            acquireResultBuilder.failureType(AcquireResult.FailureType.TIME_OUT);
        }

        return acquireResultBuilder.build();
    }

    @Override
    public void release(String resourceName, String resourceValue) {
        RLock[] rLocks = Arrays.stream(redissonClients)
                .map(redissonClient -> redissonClient.getLock(resourceName))
                .collect(Collectors.toList())
                .toArray(new RLock[]{});
        RLock redLock = redissonClients[0].getRedLock(rLocks);
        redLock.unlock();
    }
}
