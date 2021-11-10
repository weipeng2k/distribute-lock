package io.github.weipeng2k.distribute.lock.spi;

/**
 * <pre>
 * 获取锁的上下文
 *
 * 锁资源和值，以及尝试获取的起止时间
 * </pre>
 *
 * @author weipeng2k 2021年11月09日 下午17:54:58
 */
public interface AcquireContext {

    String getResourceName();

    String getResourceValue();

    long getStartNanoTime();

    long getEndNanoTime();

    default long getRemainingNanoTime() {
        return getEndNanoTime() - System.nanoTime();
    }
}
