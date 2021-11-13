package io.github.weipeng2k.distribute.lock.classic;

import io.github.weipeng2k.distribute.lock.client.DistributeLock;
import io.github.weipeng2k.distribute.lock.client.DistributeLockManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.UUID;

/**
 * @author weipeng2k 2021年11月13日 下午19:25:49
 */
public class DistributeLockManagerImpl implements DistributeLockManager {

    private final RedisCommands<String, String> syncCommands;
    /**
     * spin最小时间，毫秒
     */
    private final int minSpinMillis;
    /**
     * 随机spin增加的时间，毫秒
     */
    private final int randomMillis;
    /**
     * 占据key的时间，秒
     */
    private final int ownSecond;

    public DistributeLockManagerImpl(String host, int port, int minSpinMillis, int randomMillis, int ownSecond) {
        RedisURI redisURI = RedisURI.create(host, port);
        syncCommands = RedisClient.create(redisURI).connect().sync();
        this.minSpinMillis = minSpinMillis;
        this.randomMillis = randomMillis;
        this.ownSecond = ownSecond;
    }


    @Override
    public DistributeLock getLock(String resourceName) {
        DistributeLockImpl distributeLock = new DistributeLockImpl(syncCommands, resourceName,
                UUID.randomUUID().toString());
        distributeLock.setOwnSecond(ownSecond);
        distributeLock.setRandomMillis(randomMillis);
        distributeLock.setMinSpinMillis(minSpinMillis);
        return distributeLock;
    }
}
