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
     * 游标
     */
    private int forwardIdx;
    /**
     * 回退
     */
    private int backwardIdx;

    public Chain(List<LockHandler> handlerList) {
        this.handlerList = handlerList;
        forwardIdx = 0;
        backwardIdx = handlerList.size() - 1;
    }

    /**
     * 重置index
     */
    public void resetIndex() {
        forwardIdx = 0;
        backwardIdx = handlerList.size() - 1;
    }

    @Override
    public AcquireResult invoke(AcquireContext acquireContext) throws InterruptedException {
        forwardIdx++;
        if (forwardIdx < handlerList.size()) {
            return handlerList.get(forwardIdx).acquire(acquireContext, this);
        } else {
            forwardIdx = handlerList.size() - 1;
        }
        return null;
    }

    @Override
    public int getAcquireCurrentIndex() {
        return forwardIdx;
    }

    @Override
    public void invoke(ReleaseContext releaseContext) {
        backwardIdx--;
        if (backwardIdx >= 0) {
            handlerList.get(backwardIdx).release(releaseContext, this);
        } else {
            backwardIdx = 0;
        }
    }

    @Override
    public int getReleaseCurrentIndex() {
        return backwardIdx;
    }
}
