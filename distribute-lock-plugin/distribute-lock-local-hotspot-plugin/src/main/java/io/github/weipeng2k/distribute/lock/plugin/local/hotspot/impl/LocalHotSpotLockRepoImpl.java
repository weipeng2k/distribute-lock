package io.github.weipeng2k.distribute.lock.plugin.local.hotspot.impl;

import io.github.weipeng2k.distribute.lock.plugin.local.hotspot.LocalHotSpotLockRepo;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <pre>
 * 基于JDK {@link ReentrantLock}的Repo实现
 * </pre>
 *
 * @author weipeng2k 2021年12月14日 下午19:04:18
 */
public class LocalHotSpotLockRepoImpl implements LocalHotSpotLockRepo {

    private final ConcurrentMap<String, Lock> repo = new ConcurrentHashMap<>();

    @Override
    public Lock createLock(String resourceName) {
        return Optional.ofNullable(resourceName)
                .map(this::createLockInternal)
                .orElse(null);
    }

    private Lock createLockInternal(String resourceName) {
        return repo.computeIfAbsent(resourceName, rn -> new ReentrantLock());
    }

    @Override
    public Lock getLock(String resourceName) {
        return Optional.ofNullable(resourceName)
                .map(repo::get)
                .orElse(null);
    }

    @Override
    public Lock removeLock(String resourceName) {
        return Optional.ofNullable(resourceName)
                .map(repo::remove)
                .orElse(null);
    }

    @Override
    public void clearLocks() {
        repo.clear();
    }
}
