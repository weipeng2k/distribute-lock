package io.github.weipeng2k.distribute.lock.client.impl;

import io.github.weipeng2k.distribute.lock.client.DistributeLock;
import io.github.weipeng2k.distribute.lock.client.DistributeLockManager;
import io.github.weipeng2k.distribute.lock.spi.LockHandlerFactory;

import java.util.Objects;
import java.util.UUID;

/**
 * @author weipeng2k 2021年11月11日 上午11:15:58
 */
public class DistributeLockManagerImpl implements DistributeLockManager {

    /**
     * LockHandlerFactory
     */
    private final LockHandlerFactory lockHandlerFactory;

    public DistributeLockManagerImpl(LockHandlerFactory lockHandlerFactory) {
        this.lockHandlerFactory = lockHandlerFactory;
    }

    @Override
    public DistributeLock getLock(String resourceName) {
        Objects.requireNonNull(resourceName, "resourceName is null");

        return new DistributeLockImpl(resourceName, UUID.randomUUID().toString(), lockHandlerFactory);
    }
}
