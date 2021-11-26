package io.github.weipeng2k.distribute.lock.support.redis;

import io.github.weipeng2k.distribute.lock.spi.AcquireResult;
import io.github.weipeng2k.distribute.lock.spi.LockRemoteResource;
import io.github.weipeng2k.distribute.lock.spi.support.AcquireResultBuilder;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * 基于Redis的锁实现
 * </pre>
 *
 * @author weipeng2k 2021年11月12日 下午22:53:20
 */
public class RedissonLockRemoteResource implements LockRemoteResource {

    private final RedissonClient redisson;

    private final int ownSecond;

    public RedissonLockRemoteResource(String address, int ownSecond) {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(address);

        redisson = Redisson.create(config);
        this.ownSecond = ownSecond;
    }

    @Override
    public AcquireResult tryAcquire(String resourceName, String resourceValue, long waitTime,
                                    TimeUnit timeUnit) throws InterruptedException {
        RLock lock = redisson.getLock(resourceName);

        Integer liveSecond = OwnSecond.getLiveSecond();

        long ownTime = timeUnit.convert(liveSecond != null ? liveSecond : ownSecond, TimeUnit.SECONDS);
        AcquireResultBuilder acquireResultBuilder;

        boolean ret = lock.tryLock(waitTime, ownTime, timeUnit);
        acquireResultBuilder = new AcquireResultBuilder(ret);
        if (!ret) {
            acquireResultBuilder.failureType(AcquireResult.FailureType.TIME_OUT);
        }

        return acquireResultBuilder.build();
    }

    @Override
    public void release(String resourceName, String resourceValue) {
        RLock lock = redisson.getLock(resourceName);
        lock.unlock();
    }
}
