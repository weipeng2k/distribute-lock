package io.github.weipeng2k.distribute.lock.client.impl;

import io.github.weipeng2k.distribute.lock.client.DistributeLock;
import io.github.weipeng2k.distribute.lock.spi.AcquireContext;
import io.github.weipeng2k.distribute.lock.spi.AcquireResult;
import io.github.weipeng2k.distribute.lock.spi.ErrorAware;
import io.github.weipeng2k.distribute.lock.spi.LockHandler;
import io.github.weipeng2k.distribute.lock.spi.LockHandlerFactory;
import io.github.weipeng2k.distribute.lock.spi.ReleaseContext;
import io.github.weipeng2k.distribute.lock.spi.support.AcquireContextBuilder;
import io.github.weipeng2k.distribute.lock.spi.support.ReleaseContextBuilder;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * 用户使用分布式锁的入口
 * </pre>
 *
 * @author weipeng2k 2021年11月10日 下午21:56:40
 */
class DistributeLockImpl implements DistributeLock {
    /**
     * LockHandlerFactory
     */
    private final LockHandlerFactory lockHandlerFactory;
    /**
     * 锁的名称
     */
    private final String resourceName;
    /**
     * 锁的值
     */
    private final String resourceValue;
    /**
     * 锁的状态
     */
    private volatile boolean locked;

    DistributeLockImpl(String resourceName, String resourceValue, LockHandlerFactory lockHandlerFactory) {
        this.resourceName = resourceName;
        this.resourceValue = resourceValue;
        this.lockHandlerFactory = lockHandlerFactory;
    }

    @Override
    public boolean tryLock(long waitTime, TimeUnit unit) {
        Objects.requireNonNull(unit, "TimeUnit is null.");

        LockHandler headLockHandler = lockHandlerFactory.getHead();
        AcquireContext acquireContext = new AcquireContextBuilder(resourceName, resourceValue)
                .start(System.nanoTime())
                .timeout(waitTime, unit)
                .build();
        LockHandler.AcquireChain acquireChain = lockHandlerFactory.getAcquireChain();

        try {
            AcquireResult acquireResult = headLockHandler.acquire(acquireContext, acquireChain);
            if (acquireResult.isSuccess()) {
                locked = true;
            }
            return acquireResult.isSuccess();
        } catch (Throwable ex) {
            errorNotify(acquireContext, acquireChain, ex);
            throw new RuntimeException(
                    "acquire distribute lock [" + resourceName + ", " + resourceValue + "] got exception", ex);
        }
    }

    /**
     * <pre>
     * 当出现错误时，通知相关的LockHandler
     * </pre>
     *
     * @param acquireContext 获取上下文
     * @param acquireChain   chain
     * @param ex             异常
     */
    private void errorNotify(AcquireContext acquireContext, LockHandler.AcquireChain acquireChain, Throwable ex) {
        List<LockHandler> handlers = lockHandlerFactory.getHandlers();
        for (int i = 0; i <= acquireChain.getAcquireCurrentIndex(); i++) {
            try {
                LockHandler handler = handlers.get(i);
                if (handler instanceof ErrorAware) {
                    ((ErrorAware) handler).onAcquireError(acquireContext, ex);
                }
            } catch (Throwable throwable) {
                // Ignore.
            }
        }
    }

    @Override
    public void unlock() {
        if (locked) {
            LockHandler tailLockHandler = lockHandlerFactory.getTail();
            ReleaseContext releaseContext = new ReleaseContextBuilder(resourceName, resourceValue)
                    .start(System.nanoTime())
                    .build();

            LockHandler.ReleaseChain releaseChain = lockHandlerFactory.getReleaseChain();

            try {
                tailLockHandler.release(releaseContext, releaseChain);
                locked = false;
            } catch (Throwable ex) {
                List<LockHandler> handlers = lockHandlerFactory.getHandlers();
                errorNotify(releaseContext, releaseChain, ex, handlers);
                throw new RuntimeException(
                        "release distribute lock [" + resourceName + ", " + resourceValue + "] got exception", ex);
            }
        }
    }

    /**
     * <pre>
     * 当出现错误时，通知相关的LockHandler
     * </pre>
     *
     * @param releaseContext 释放上下文
     * @param releaseChain   chain
     * @param ex             异常
     */
    private void errorNotify(ReleaseContext releaseContext, LockHandler.ReleaseChain releaseChain, Throwable ex,
                             List<LockHandler> handlers) {
        for (int i = handlers.size() - 1; i >= releaseChain.getReleaseCurrentIndex(); i--) {
            try {
                LockHandler handler = handlers.get(i);
                if (handler instanceof ErrorAware) {
                    ((ErrorAware) handler).onReleaseError(releaseContext, ex);
                }
            } catch (Throwable throwable) {
                // Ignore.
            }
        }
    }
}
