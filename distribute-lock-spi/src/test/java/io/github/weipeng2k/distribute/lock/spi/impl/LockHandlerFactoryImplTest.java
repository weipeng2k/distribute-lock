package io.github.weipeng2k.distribute.lock.spi.impl;

import io.github.weipeng2k.distribute.lock.spi.AcquireContext;
import io.github.weipeng2k.distribute.lock.spi.AcquireResult;
import io.github.weipeng2k.distribute.lock.spi.LockHandler;
import io.github.weipeng2k.distribute.lock.spi.LockHandlerFactory;
import io.github.weipeng2k.distribute.lock.spi.LockRemoteResource;
import io.github.weipeng2k.distribute.lock.spi.ReleaseContext;
import io.github.weipeng2k.distribute.lock.spi.support.AcquireContextBuilder;
import io.github.weipeng2k.distribute.lock.spi.support.AcquireResultBuilder;
import io.github.weipeng2k.distribute.lock.spi.support.ReleaseContextBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author weipeng2k 2021年11月10日 下午21:08:54
 */
public class LockHandlerFactoryImplTest {

    static LockHandlerFactory lockHandlerFactory;

    @BeforeClass
    public static void init() {
        lockHandlerFactory = new LockHandlerFactoryImpl(
                Arrays.asList(new TestHandler("1"), new TestHandler("2"), new TestHandler("3")),
                new LockRemoteResource() {
                    @Override
                    public AcquireResult tryAcquire(String resourceName, String resourceValue, long waitTime,
                                                    TimeUnit timeUnit) {
                        return new AcquireResultBuilder(true)
                                .build();
                    }

                    @Override
                    public void release(String resourceName, String resourceValue) {

                    }
                });
    }

    @Test
    public void getHead() {
        Assert.assertNotNull(lockHandlerFactory.getHead());
    }

    @Test
    public void getTail() {
        Assert.assertNotNull(lockHandlerFactory.getTail());
    }

    @Test
    public void getAcquireChain() throws Exception {
        AcquireContextBuilder acquireContextBuilder = new AcquireContextBuilder("R", "V");
        AcquireContext acquireContext = acquireContextBuilder
                .start(System.nanoTime())
                .timeout(3, TimeUnit.SECONDS)
                .build();

        AcquireResult acquire = lockHandlerFactory.getHead().acquire(acquireContext,
                lockHandlerFactory.getAcquireChain());
        Assert.assertTrue(acquire.isSuccess());
    }

    @Test
    public void getReleaseChain() {
        ReleaseContextBuilder releaseContextBuilder = new ReleaseContextBuilder("R", "V");
        ReleaseContext releaseContext = releaseContextBuilder.build();
        lockHandlerFactory.getTail().release(releaseContext,
                lockHandlerFactory.getReleaseChain());
    }

    @Test
    public void getHandlers() {
        Assert.assertFalse(lockHandlerFactory.getHandlers().isEmpty());
    }

    static class TestHandler implements LockHandler {

        private final String name;

        TestHandler(String name) {
            this.name = name;
        }

        @Override
        public AcquireResult acquire(AcquireContext acquireContext, AcquireChain acquireChain) throws InterruptedException {
            System.out.println("Enter " + name + ", before acquire");
            try {
                return acquireChain.invoke(acquireContext);
            } finally {
                System.out.println("Leave " + name + ", after acquire");
            }
        }

        @Override
        public void release(ReleaseContext releaseContext, ReleaseChain releaseChain) {
            System.out.println("Enter " + name + ", before release");
            releaseChain.invoke(releaseContext);
            System.out.println("Leave " + name + ", after release");
        }
    }
}