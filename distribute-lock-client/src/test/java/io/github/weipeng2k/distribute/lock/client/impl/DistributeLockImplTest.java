package io.github.weipeng2k.distribute.lock.client.impl;

import io.github.weipeng2k.distribute.lock.spi.AcquireContext;
import io.github.weipeng2k.distribute.lock.spi.AcquireResult;
import io.github.weipeng2k.distribute.lock.spi.LockHandler;
import io.github.weipeng2k.distribute.lock.spi.LockHandlerFactory;
import io.github.weipeng2k.distribute.lock.spi.LockRemoteResource;
import io.github.weipeng2k.distribute.lock.spi.ReleaseContext;
import io.github.weipeng2k.distribute.lock.spi.impl.LockHandlerFactoryImpl;
import io.github.weipeng2k.distribute.lock.spi.support.AcquireResultBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author weipeng2k 2021年11月11日 上午11:16:21
 */
public class DistributeLockImplTest {

    DistributeLockImpl distributeLock;

    @Before
    public void init() {
        LockHandlerFactory lockHandlerFactory = new LockHandlerFactoryImpl(
                List.of(new TestHandler("1")),
                new LockRemoteResource() {

                    final Lock lock = new ReentrantLock();

                    @Override
                    public AcquireResult tryAcquire(String resourceName, String resourceValue, long waitTime,
                                                    TimeUnit timeUnit) {
                        boolean result = false;
                        try {
                            System.err.println(Thread.currentThread() + "start@" + new Date());
                            result = lock.tryLock(waitTime, timeUnit);
                            System.err.println(Thread.currentThread() + "end@" + new Date());
                        } catch (Exception ex) {
                            // Ignore.
                        }
                        AcquireResultBuilder acquireResultBuilder = new AcquireResultBuilder(result);
                        return acquireResultBuilder.build();
                    }

                    @Override
                    public void release(String resourceName, String resourceValue) {
                        lock.unlock();
                    }
                });
        distributeLock = new DistributeLockImpl("x", "y", lockHandlerFactory);
    }

    @Test
    public void tryLock() throws Exception{
        // 1st can get the lock
        Assert.assertTrue(distributeLock.tryLock(3, TimeUnit.SECONDS));

        // 2nd not get
        Thread thread = new Thread(() -> {
            Assert.assertTrue(distributeLock.tryLock(3, TimeUnit.SECONDS));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            distributeLock.unlock();
        });

        thread.start();
        Thread.sleep(1000);

        // 1st thread unlock & 2nd thread have it.
        distributeLock.unlock();

        Thread.sleep(5000);
    }

    @Test
    public void tryLockFail() throws Exception {
        // 1st can get the lock
        Assert.assertTrue(distributeLock.tryLock(3, TimeUnit.SECONDS));

        // 2nd not get
        Thread thread = new Thread(() -> {
            Assert.assertFalse(distributeLock.tryLock(3, TimeUnit.SECONDS));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            distributeLock.unlock();

            Assert.assertTrue(distributeLock.tryLock(3, TimeUnit.SECONDS));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            distributeLock.unlock();
        });

        thread.start();
        Thread.sleep(4000);

        // 1st thread unlock & 2nd thread have it.
        distributeLock.unlock();

        Thread.sleep(3000);
    }

    @Test
    public void unlock() {
    }

    static class TestHandler implements LockHandler {

        private final String name;

        TestHandler(String name) {
            this.name = name;
        }

        @Override
        public AcquireResult acquire(AcquireContext acquireContext, AcquireChain acquireChain) {
            System.out.println(Thread.currentThread() + "Enter " + name + ", before acquire@" + new Date());
            try {
                return acquireChain.invoke(acquireContext);
            } finally {
                System.out.println(Thread.currentThread() + "Leave " + name + ", after acquire@"+ new Date());
            }
        }

        @Override
        public void release(ReleaseContext releaseContext, ReleaseChain releaseChain) {
            System.out.println(Thread.currentThread() + "Enter " + name + ", before release@"+ new Date());
            releaseChain.invoke(releaseContext);
            System.out.println(Thread.currentThread() + "Leave " + name + ", after release@"+ new Date());
        }
    }
}