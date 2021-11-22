package io.github.weipeng2k.distribute.lock.spi;

/**
 * <pre>
 * 释放锁上下文
 * </pre>
 *
 * @author weipeng2k 2021年11月09日 下午17:56:27
 */
public interface ReleaseContext {

    /**
     * 获取锁资源名称
     *
     * @return 锁资源名称
     */
    String getResourceName();

    /**
     * 获取锁资源值
     *
     * @return 锁资源值
     */
    String getResourceValue();
}
