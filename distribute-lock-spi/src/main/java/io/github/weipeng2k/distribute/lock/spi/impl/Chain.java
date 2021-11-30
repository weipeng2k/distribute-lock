package io.github.weipeng2k.distribute.lock.spi.impl;

import io.github.weipeng2k.distribute.lock.spi.AcquireContext;
import io.github.weipeng2k.distribute.lock.spi.AcquireResult;
import io.github.weipeng2k.distribute.lock.spi.LockHandler;
import io.github.weipeng2k.distribute.lock.spi.ReleaseContext;

import java.util.List;

/**
 * 获取锁/释放锁的Chain实现
 *
 * @author weipeng2k 2021年11月10日 下午16:55:34
 */
final class Chain implements LockHandler.AcquireChain, LockHandler.ReleaseChain {

    /**
     * 处理的Handler列表
     */
    private final List<LockHandler> handlerList;
    /**
     * 获取锁的游标
     */
    private int acquireIdx;
    /**
     * 释放锁的游标
     */
    private int releaseIdx;

    public Chain(List<LockHandler> handlerList) {
        this.handlerList = handlerList;
        acquireIdx = 0;
        releaseIdx = 0;
    }

    /**
     * 重置index
     */
    public void resetAcquireIndex() {
        acquireIdx = 0;
    }

    public void resetReleaseIndex() {
        releaseIdx = 0;
    }

    @Override
    public AcquireResult invoke(AcquireContext acquireContext) throws InterruptedException {
        acquireIdx++;
        if (acquireIdx < handlerList.size()) {
            return handlerList.get(acquireIdx).acquire(acquireContext, this);
        } else {
            acquireIdx = handlerList.size() - 1;
        }
        return null;
    }

    @Override
    public int getAcquireCurrentIndex() {
        return acquireIdx;
    }

    @Override
    public void invoke(ReleaseContext releaseContext) {
        releaseIdx++;
        if (releaseIdx < handlerList.size()) {
            handlerList.get(releaseIdx).release(releaseContext, this);
        } else {
            releaseIdx = handlerList.size() - 1;
        }
    }

    @Override
    public int getReleaseCurrentIndex() {
        return releaseIdx;
    }
}
