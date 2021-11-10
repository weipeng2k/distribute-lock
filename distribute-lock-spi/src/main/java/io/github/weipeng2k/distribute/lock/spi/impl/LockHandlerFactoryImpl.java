package io.github.weipeng2k.distribute.lock.spi.impl;

import io.github.weipeng2k.distribute.lock.spi.AcquireContext;
import io.github.weipeng2k.distribute.lock.spi.AcquireResult;
import io.github.weipeng2k.distribute.lock.spi.LockHandler;
import io.github.weipeng2k.distribute.lock.spi.LockHandlerFactory;
import io.github.weipeng2k.distribute.lock.spi.LockRemoteResource;
import io.github.weipeng2k.distribute.lock.spi.ReleaseContext;
import io.github.weipeng2k.distribute.lock.spi.support.AcquireResultBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author weipeng2k 2021年11月10日 下午17:56:43
 */
public class LockHandlerFactoryImpl implements LockHandlerFactory {

    // avoid too much little objs in heap, reuse Chain
    private final ThreadLocal<Chain> HANDLER_CHAIN_THREAD_LOCAL;

    private final List<LockHandler> handlers;

    private final int headIndex;

    private final int tailIndex;

    public LockHandlerFactoryImpl(List<LockHandler> lockHandlerList, LockRemoteResource lockRemoteResource) {
        handlers = new ArrayList<>(lockHandlerList);
        handlers.add(new TailLockHandler(lockRemoteResource));
        headIndex = 0;
        tailIndex = handlers.size() - 1;
        HANDLER_CHAIN_THREAD_LOCAL = ThreadLocal.withInitial(() -> new Chain(handlers));
    }

    @Override
    public LockHandler getHead() {
        return handlers.get(headIndex);
    }

    @Override
    public LockHandler getTail() {
        return handlers.get(tailIndex);
    }

    @Override
    public LockHandler.AcquireChain getAcquireChain() {
        return getChain();
    }

    @Override
    public LockHandler.ReleaseChain getReleaseChain() {
        return getChain();
    }

    private Chain getChain() {
        Chain chain = HANDLER_CHAIN_THREAD_LOCAL.get();
        chain.resetIndex();
        return chain;
    }

    @Override
    public List<LockHandler> getHandlers() {
        return handlers;
    }

    private static class TailLockHandler implements LockHandler {

        private final LockRemoteResource lockRemoteResource;

        TailLockHandler(LockRemoteResource lockRemoteResource) {
            this.lockRemoteResource = lockRemoteResource;
        }

        @Override
        public AcquireResult acquire(AcquireContext acquireContext, AcquireChain acquireChain) {
            long waitTime = acquireContext.getRemainingNanoTime();
            if (waitTime > 0) {
                try {
                    return lockRemoteResource.tryAcquire(acquireContext.getResourceName(),
                            acquireContext.getResourceValue(), waitTime, TimeUnit.NANOSECONDS);
                } catch (Throwable ex) {
                    AcquireResultBuilder acquireResultBuilder = new AcquireResultBuilder(false);
                    return acquireResultBuilder
                            .failureType(AcquireResult.FailureType.EXCEPTION)
                            .failureMessage("acquire lock remote resource got exception")
                            .exception(ex)
                            .build();
                }
            } else {
                AcquireResultBuilder acquireResultBuilder = new AcquireResultBuilder(false);
                return acquireResultBuilder
                        .failureType(AcquireResult.FailureType.TIME_OUT)
                        .build();
            }
        }

        @Override
        public void release(ReleaseContext releaseContext, ReleaseChain releaseChain) {
            releaseChain.invoke(releaseContext);
        }
    }

}
