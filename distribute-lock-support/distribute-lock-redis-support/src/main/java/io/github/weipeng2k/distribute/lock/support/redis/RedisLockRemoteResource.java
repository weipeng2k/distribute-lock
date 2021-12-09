package io.github.weipeng2k.distribute.lock.support.redis;

import io.github.weipeng2k.distribute.lock.spi.AcquireResult;
import io.github.weipeng2k.distribute.lock.spi.LockRemoteResource;
import io.github.weipeng2k.distribute.lock.spi.support.AcquireResultBuilder;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * 基于Redis的锁实现
 * </pre>
 *
 * @author weipeng2k 2021年11月12日 下午22:53:20
 */
public class RedisLockRemoteResource implements LockRemoteResource {

    /**
     * Redis Client
     */
    private final RedisCommands<String, String> syncCommands;
    /**
     * 占据key的时间，秒
     */
    private final int ownSecond;
    /**
     * spin最小时间，毫秒
     */
    private final int minSpinMillis;
    /**
     * 随机spin增加的时间，毫秒
     */
    private final int randomMillis;

    public RedisLockRemoteResource(String address, int ownSecond, int minSpinMillis, int randomMillis) {
        RedisURI redisURI = RedisURI.create(address);
        this.syncCommands = RedisClient.create(redisURI).connect().sync();
        this.ownSecond = ownSecond;
        this.minSpinMillis = minSpinMillis;
        this.randomMillis = randomMillis;
    }

    @Override
    public AcquireResult tryAcquire(String resourceName, String resourceValue, long waitTime,
                                    TimeUnit timeUnit) throws InterruptedException {
        Objects.requireNonNull(timeUnit, "TimeUnit is null");
        // 目标最大超时时间
        long destinationNanoTime = System.nanoTime() + timeUnit.toNanos(waitTime);
        boolean result = false;
        boolean isTimeout = false;

        Integer liveSecond = OwnSecond.getLiveSecond();
        int ownTime = liveSecond != null ? liveSecond : ownSecond;

        AcquireResultBuilder acquireResultBuilder;

        try {
            while (true) {
                // 当前系统时间
                long current = System.nanoTime();
                // 时间限度外，直接退出
                if (current > destinationNanoTime) {
                    isTimeout = true;
                    break;
                }
                // 远程获取到资源后，返回；否则，spin
                if (lockRemoteResource(resourceName, resourceValue, ownTime)) {
                    result = true;
                    break;
                } else {
                    spin();
                }
            }
            acquireResultBuilder = new AcquireResultBuilder(result);
            if (isTimeout) {
                acquireResultBuilder.failureType(AcquireResult.FailureType.TIME_OUT);
            }
        } catch (Exception ex) {
            acquireResultBuilder = new AcquireResultBuilder(result);
            acquireResultBuilder
                    .failureType(AcquireResult.FailureType.EXCEPTION)
                    .exception(ex);
        }

        return acquireResultBuilder.build();
    }

    @Override
    public void release(String resourceName, String resourceValue) {
        try {
            syncCommands.eval(
                    "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end",
                    ScriptOutputType.INTEGER, new String[]{resourceName}, resourceValue);
        } catch (Exception ex) {
            // Ignore.
        }
    }

    /**
     * 锁定远端资源
     *
     * @param resourceName  资源名称
     * @param resourceValue 资源值
     * @param ownSecond     占据时间
     * @return 是否锁定，true表示获取成功
     */
    private boolean lockRemoteResource(String resourceName, String resourceValue, int ownSecond) {
        SetArgs setArgs = SetArgs.Builder.nx().ex(ownSecond);
        boolean result = false;
        try {
            String ret = syncCommands.set(resourceName, resourceValue, setArgs);
            // 返回是OK，则锁定成功，否则锁定资源失败
            if ("ok".equalsIgnoreCase(ret)) {
                result = true;
            }
        } catch (Exception ex) {
            // Ignore.
        }

        return result;
    }

    /**
     * 自旋等待
     */
    private void spin() throws InterruptedException{
        long sleepMillis = new Random().nextInt(randomMillis) + minSpinMillis;
        TimeUnit.MILLISECONDS.sleep(sleepMillis);
    }
}
