package io.github.weipeng2k.distribute.lock.client;

/**
 * <pre>
 * 分布式锁Manager，根据资源名称获取一个分布式锁
 *
 * </pre>
 *
 * @author weipeng2k 2021年11月07日 下午18:24:46
 */
public interface DistributeLockManager {

    /**
     * <pre>
     * 获取资源对应的分布式锁
     *
     * 使用方式：
     *
     * <code>DistributeLock lock = distributeLockManager.getLock("lock_name");</code>
     * <code>if (lock.tryLock(1, TimeUnit.SECONDS)) {</code>
     * <code>    try {</code>
     * <code>        // do sth...</code>
     * <code>    } finally {</code>
     * <code>        lock.unlock();</code>
     * <code>    } </code>
     * <code>}</code>
     * </pre>
     *
     * @param resourceName 资源名称，不能为空
     * @return 分布式锁
     */
    DistributeLock getLock(String resourceName);
}
