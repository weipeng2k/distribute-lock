package io.github.weipeng2k.distribute.lock.support.zookeeper;

import io.github.weipeng2k.distribute.lock.spi.AcquireResult;
import io.github.weipeng2k.distribute.lock.spi.LockRemoteResource;
import io.github.weipeng2k.distribute.lock.spi.support.AcquireResultBuilder;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @author weipeng2k 2021年11月14日 下午22:30:34
 */
public class ZooKeeperLockRemoteResource implements LockRemoteResource {

    private CuratorFramework curatorFramework;

    private ConcurrentMap<String, InterProcessMutex> lockRepo = new ConcurrentHashMap<>();

    public ZooKeeperLockRemoteResource(String connectString, int baseSleepTimeMs, int maxRetries, int sessionTimeoutMs,
                                       int connectionTimeoutMs) {
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .sessionTimeoutMs(sessionTimeoutMs)
                .connectionTimeoutMs(connectionTimeoutMs)
                .retryPolicy(new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries))
                .build();
        curatorFramework.start();
    }

    @Override
    public AcquireResult tryAcquire(String resourceName, String resourceValue, long waitTime,
                                    TimeUnit timeUnit) throws InterruptedException {
        InterProcessMutex lock = lockRepo.computeIfAbsent(resourceName,
                rn -> new InterProcessMutex(curatorFramework, "/" + rn));

        AcquireResultBuilder acquireResultBuilder;
        try {
            boolean ret = lock.acquire(waitTime, timeUnit);
            acquireResultBuilder = new AcquireResultBuilder(ret);
            if (!ret) {
                acquireResultBuilder.failureType(AcquireResult.FailureType.TIME_OUT);
            }
            return acquireResultBuilder.build();
        } catch (Exception ex) {
            throw new RuntimeException("acquire zk lock got exception.", ex);
        }
    }

    @Override
    public void release(String resourceName, String resourceValue) {
        InterProcessMutex lock = lockRepo.computeIfAbsent(resourceName,
                rn -> new InterProcessMutex(curatorFramework, "/" + rn));
        try {
            lock.release();
        } catch (Exception ex) {
            throw new RuntimeException("release zk lock got exception.", ex);
        }
    }
}
