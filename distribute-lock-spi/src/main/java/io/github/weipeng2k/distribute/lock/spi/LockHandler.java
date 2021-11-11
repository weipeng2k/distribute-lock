package io.github.weipeng2k.distribute.lock.spi;

/**
 * @author weipeng2k 2021年11月07日 下午21:03:56
 */
public interface LockHandler {

    /**
     * <pre>
     * 获取锁资源的处理逻辑
     *
     * 实现该方法时，如果是可期望的异常，尽量需要自行捕获
     * 如果抛出异常，将会使异常抵达客户端调用处
     * </pre>
     *
     * @param acquireContext 获取上下文
     * @param acquireChain   chain
     * @return 获取结果
     */
    AcquireResult acquire(AcquireContext acquireContext, AcquireChain acquireChain);

    /**
     * <pre>
     * 获取锁后，在释放锁时，会调用该方法
     * </pre>
     *
     * @param releaseContext 释放上下文
     * @param releaseChain   chain
     */
    void release(ReleaseContext releaseContext, ReleaseChain releaseChain);

    interface AcquireChain {
        AcquireResult invoke(AcquireContext acquireContext);

        int getAcquireCurrentIndex();
    }

    interface ReleaseChain {
        void invoke(ReleaseContext releaseContext);

        int getReleaseCurrentIndex();
    }
}
