package io.github.weipeng2k.distribute.lock.classic;

import io.github.weipeng2k.distribute.lock.client.DistributeLock;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author weipeng2k 2021年11月13日 下午19:27:02
 */
class DistributeLockImpl implements DistributeLock {
    /**
     * Redis Client
     */
    private final RedisCommands<String, String> syncCommands;
    /**
     * 资源名称，对应的Redis Key
     */
    private final String resourceName;
    /**
     * 资源对应的值，当前线程需要设置的值
     */
    private final String resourceValue;
    /**
     * spin最小时间，毫秒
     */
    private int minSpinMillis;
    /**
     * 随机spin增加的时间，毫秒
     */
    private int randomMillis;
    /**
     * 占据key的时间，秒
     */
    private int ownSecond;
    /**
     * 是否锁定
     */
    private volatile boolean locked;

    public DistributeLockImpl(RedisCommands<String, String> syncCommands, String resourceName, String resourceValue) {
        this.syncCommands = syncCommands;
        this.resourceName = resourceName;
        this.resourceValue = resourceValue;
    }

    public void setOwnSecond(int ownSecond) {
        this.ownSecond = ownSecond;
    }

    public void setMinSpinMillis(int minSpinMillis) {
        this.minSpinMillis = minSpinMillis;
    }

    public void setRandomMillis(int randomMillis) {
        this.randomMillis = randomMillis;
    }


    @Override
    public boolean tryLock(long waitTime, TimeUnit unit) {
        Objects.requireNonNull(unit, "TimeUnit is null");
        // 目标最大超时时间
        long destinationNanoTime = System.nanoTime() + unit.toNanos(waitTime);
        boolean result = false;

        try {
            while (true) {
                // 当前系统时间
                long current = System.nanoTime();
                // 时间限度外，直接退出
                if (current > destinationNanoTime) {
                    break;
                }
                // 远程获取到资源后，返回；否则，spin
                if (lockRemoteResource(ownSecond)) {
                    result = true;
                    break;
                } else {
                    spinUnInterrupt();
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("acquire distribute lock got exception", ex);
        }

        return result;
    }

    @Override
    public void unlock() {
        if (locked) {
            try {
                syncCommands.eval(
                        "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end",
                        ScriptOutputType.INTEGER, new String[]{resourceName}, resourceValue);
                locked = false;
            } catch (Exception ex) {
                // Ignore.
            }
        }
    }

    /**
     * 锁定远端资源
     *
     * @param ownSecond 占据时间
     * @return 是否锁定，true表示获取成功
     */
    private boolean lockRemoteResource(int ownSecond) {
        SetArgs setArgs = SetArgs.Builder.nx().ex(ownSecond);
        boolean result = false;
        try {
            String ret = syncCommands.set(resourceName, resourceValue, setArgs);
            // 返回是OK，则锁定成功，否则锁定资源失败
            if ("ok".equalsIgnoreCase(ret)) {
                locked = true;
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
    private void spinUnInterrupt() {
        try {
            long sleepMillis = new Random().nextInt(randomMillis) + minSpinMillis;
            TimeUnit.MILLISECONDS.sleep(sleepMillis);
        } catch (Exception ex) {
            // Ignore.
        }
    }

}
