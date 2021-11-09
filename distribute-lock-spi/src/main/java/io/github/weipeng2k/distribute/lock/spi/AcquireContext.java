package io.github.weipeng2k.distribute.lock.spi;

/**
 * @author weipeng2k 2021年11月09日 下午17:54:58
 */
public interface AcquireContext {

    String getResourceName();

    String getResourceValue();

    long getStartNanoTime();

    long getEndNanoTime();
}
