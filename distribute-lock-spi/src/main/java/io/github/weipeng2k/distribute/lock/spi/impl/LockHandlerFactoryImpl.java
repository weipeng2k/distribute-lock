package io.github.weipeng2k.distribute.lock.spi.impl;

import io.github.weipeng2k.distribute.lock.spi.AcquireContext;
import io.github.weipeng2k.distribute.lock.spi.AcquireResult;
import io.github.weipeng2k.distribute.lock.spi.LockHandler;
import io.github.weipeng2k.distribute.lock.spi.LockHandlerFactory;
import io.github.weipeng2k.distribute.lock.spi.LockRemoteResource;
import io.github.weipeng2k.distribute.lock.spi.ReleaseContext;
import io.github.weipeng2k.distribute.lock.spi.support.AcquireResultBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * LockHandlerFactory实现，完成{@link LockHandler}链的构造。
 *
 * </pre>
 *
 * @author weipeng2k 2021年11月10日 下午17:56:43
 */
public class LockHandlerFactoryImpl implements LockHandlerFactory {

    // avoid too much little objs in heap, reuse Chain
    private final ThreadLocal<Chain> HANDLER_CHAIN_THREAD_LOCAL;

    private final List<LockHandler> handlers;

    private final LockHandler head;

    private final LockHandler tail;

    public LockHandlerFactoryImpl(List<LockHandler> lockHandlerList, LockRemoteResource lockRemoteResource) {
        LinkedList<LockHandler> temp = new LinkedList<>(lockHandlerList);
        head = new HeadLockHandler();
        tail = new TailLockHandler(lockRemoteResource);
        temp.addFirst(head);
        temp.addLast(tail);
        handlers = new ArrayList<>(temp);
        HANDLER_CHAIN_THREAD_LOCAL = ThreadLocal.withInitial(() -> new Chain(handlers));
    }

    @Override
    public LockHandler getHead() {
        return head;
    }

    @Override
    public LockHandler getTail() {
        return tail;
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

    /**
     * 头节点
     */
    private static class HeadLockHandler implements LockHandler {

        @Override
        public AcquireResult acquire(AcquireContext acquireContext,
                                     AcquireChain acquireChain) throws InterruptedException {
            return acquireChain.invoke(acquireContext);
        }

        @Override
        public void release(ReleaseContext releaseContext, ReleaseChain releaseChain) {

        }
    }

    /**
     * 尾节点
     */
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
                    throw new RuntimeException(
                            "acquire lock remote resource:" + acquireContext.getResourceName() + " got exception", ex);
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
            try {
                lockRemoteResource.release(releaseContext.getResourceName(), releaseContext.getResourceValue());
                releaseChain.invoke(releaseContext);
            } catch (Throwable ex) {
                throw new RuntimeException(
                        "release lock remote resource:" + releaseContext.getResourceName() + " got exception", ex);
            }
        }
    }

}
