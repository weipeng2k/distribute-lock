package io.github.weipeng2k.distribute.lock.client.impl;

import io.github.weipeng2k.distribute.lock.client.DistributeLock;
import io.github.weipeng2k.distribute.lock.spi.LockHandler;
import io.github.weipeng2k.distribute.lock.spi.LockHandlerFactory;
import io.github.weipeng2k.distribute.lock.spi.support.AcquireContextBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @author weipeng2k 2021年11月10日 下午21:56:40
 */
public class DistributeLockImpl implements DistributeLock {

    private LockHandlerFactory lockHandlerFactory;

    private String resourceName;

    private String resourceValue;

    @Override
    public boolean tryLock(long waitTime, TimeUnit unit) {

        LockHandler lockHandler = lockHandlerFactory.getHead();
//        AcquireContextBuilder acquireContextBuilder = new AcquireContextBuilder()

        return false;
    }

    @Override
    public void unlock() {

    }
}
