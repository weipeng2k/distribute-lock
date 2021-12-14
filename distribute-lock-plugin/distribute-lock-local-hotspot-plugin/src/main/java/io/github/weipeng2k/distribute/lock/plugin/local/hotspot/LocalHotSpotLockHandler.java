package io.github.weipeng2k.distribute.lock.plugin.local.hotspot;

import io.github.weipeng2k.distribute.lock.spi.AcquireContext;
import io.github.weipeng2k.distribute.lock.spi.AcquireResult;
import io.github.weipeng2k.distribute.lock.spi.ErrorAware;
import io.github.weipeng2k.distribute.lock.spi.LockHandler;
import io.github.weipeng2k.distribute.lock.spi.ReleaseContext;
import io.github.weipeng2k.distribute.lock.spi.support.AcquireResultBuilder;
import org.springframework.core.annotation.Order;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * <pre>
 * 本地热点锁LocalHandler
 *
 * 获取锁时，会先获取本地的锁，然后尝试获取后面的锁
 *      如果后面的锁获取成功，则返回
 *      如果后面的锁获取失败，则需要解锁
 *
 * 释放锁时，会先释放后面的锁，然后尝试释放当前的锁，不要抛出错误即可
 *
 * </pre>
 *
 * @author weipeng2k 2021年12月14日 下午18:43:29
 */
@Order(10)
public class LocalHotSpotLockHandler implements LockHandler, ErrorAware {

    private final LocalHotSpotLockRepo localHotSpotLockRepo;

    public LocalHotSpotLockHandler(LocalHotSpotLockRepo localHotSpotLockRepo) {
        this.localHotSpotLockRepo = localHotSpotLockRepo;
    }

    @Override
    public AcquireResult acquire(AcquireContext acquireContext, AcquireChain acquireChain) throws InterruptedException {
        AcquireResult acquireResult;
        Lock lock = localHotSpotLockRepo.getLock(acquireContext.getResourceName());
        if (lock != null) {
            // 先获取本地锁
            if (lock.tryLock(acquireContext.getRemainingNanoTime(), TimeUnit.NANOSECONDS)) {
                acquireResult = acquireChain.invoke(acquireContext);
                // 没有获取到后面的锁，则进行解锁
                if (!acquireResult.isSuccess()) {
                    unlockQuietly(lock);
                }
            } else {
                AcquireResultBuilder acquireResultBuilder = new AcquireResultBuilder(false);
                acquireResult = acquireResultBuilder.failureType(AcquireResult.FailureType.TIME_OUT)
                        .build();
            }
        } else {
            acquireResult = acquireChain.invoke(acquireContext);
        }

        return acquireResult;
    }

    @Override
    public void release(ReleaseContext releaseContext, ReleaseChain releaseChain) {
        releaseChain.invoke(releaseContext);
        Lock lock = localHotSpotLockRepo.getLock(releaseContext.getResourceName());
        if (lock != null) {
            unlockQuietly(lock);
        }
    }

    @Override
    public void onAcquireError(AcquireContext acquireContext, Throwable throwable) {
        Lock lock = localHotSpotLockRepo.getLock(acquireContext.getResourceName());
        if (lock != null) {
            unlockQuietly(lock);
        }
    }

    @Override
    public void onReleaseError(ReleaseContext releaseContext, Throwable throwable) {
        Lock lock = localHotSpotLockRepo.getLock(releaseContext.getResourceName());
        if (lock != null) {
            unlockQuietly(lock);
        }
    }

    private void unlockQuietly(Lock lock) {
        try {
            lock.unlock();
        } catch (Exception ex) {
            // Ignore.
        }
    }
}
