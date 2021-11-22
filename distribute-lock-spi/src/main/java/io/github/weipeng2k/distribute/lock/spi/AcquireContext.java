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

    /**
     * 获取开始获取锁的开始时间，单位：纳秒
     *
     * @return 获取锁的开始时间，单位：纳秒
     */
    long getStartNanoTime();

    /**
     * 获取锁的最长超时时间，单位：纳秒
     *
     * @return 获取锁的最长超时时间，单位：纳秒
     */
    long getEndNanoTime();

    /**
     * 获取当前剩余的超时时间，单位：纳秒
     *
     * @return 当前剩余的超时时间，单位：纳秒
     */
    default long getRemainingNanoTime() {
        return getEndNanoTime() - System.nanoTime();
    }
}
