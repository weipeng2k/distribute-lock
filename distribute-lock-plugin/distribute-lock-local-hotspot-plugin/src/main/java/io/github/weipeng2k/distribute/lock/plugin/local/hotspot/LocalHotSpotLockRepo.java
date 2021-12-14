package io.github.weipeng2k.distribute.lock.plugin.local.hotspot;

import java.util.Optional;
import java.util.concurrent.locks.Lock;

/**
 * <pre>
 * 本地热点锁存储
 * </pre>
 *
 * @author weipeng2k 2021年12月14日 下午18:45:57
 */
public interface LocalHotSpotLockRepo {

    /**
     * 放置一个锁
     *
     * @param resourceName 资源名称
     * @return lock实例
     */
    Lock createLock(String resourceName);

    /**
     * 根据资源名称获取锁
     *
     * @param resourceName 资源名称
     * @return 锁
     */
    Lock getLock(String resourceName);

    /**
     * 删除锁
     *
     * @param resourceName 资源名称
     * @return lock实例
     */
    Lock removeLock(String resourceName);

    /**
     * 清除所有的锁
     */
    void clearLocks();

}
