package io.github.weipeng2k.distribute.lock.spi.impl;

import io.github.weipeng2k.distribute.lock.spi.AcquireContext;
import io.github.weipeng2k.distribute.lock.spi.AcquireResult;
import io.github.weipeng2k.distribute.lock.spi.ErrorAware;
import io.github.weipeng2k.distribute.lock.spi.LockHandler;
import io.github.weipeng2k.distribute.lock.spi.ReleaseContext;
import io.github.weipeng2k.distribute.lock.spi.support.AcquireContextBuilder;
import io.github.weipeng2k.distribute.lock.spi.support.AcquireResultBuilder;
import io.github.weipeng2k.distribute.lock.spi.support.ReleaseContextBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author weipeng2k 2021年11月10日 下午17:07:52
 */
public class ChainTest {

    private static final List<LockHandler> handlerList = new ArrayList<>();

    @BeforeClass
    public static void init() {
        handlerList.add(new TestHandler("T1"));
        handlerList.add(new TestHandler("T2"));
        handlerList.add(new TestHandler("T3"));
        handlerList.add(new TailHandler());
    }

    @Test
    public void acquire() throws Exception {
        AcquireContextBuilder acquireContextBuilder = new AcquireContextBuilder("R", "V");
        AcquireContext acquireContext = acquireContextBuilder
                .start(System.nanoTime())
                .timeout(3, TimeUnit.SECONDS)
                .build();

        LockHandler lockHandler = handlerList.get(0);
        Chain chain = new Chain(handlerList);
        AcquireResult acquireResult = lockHandler.acquire(acquireContext, chain);
        System.out.println(acquireResult);
        Assert.assertTrue(acquireResult.isSuccess());
        System.out.println(chain.getAcquireCurrentIndex());
    }

    @Test
    public void exception() {
        AcquireContextBuilder acquireContextBuilder = new AcquireContextBuilder("R", "V");
        AcquireContext acquireContext = acquireContextBuilder
                .start(System.nanoTime())
                .timeout(3, TimeUnit.SECONDS)
                .build();
        List<LockHandler> handlerList = new ArrayList<>();
        handlerList.add(new TestHandler("T1"));
        handlerList.add(new TestHandler("T2"));
        handlerList.add(new LockHandler() {
            @Override
            public AcquireResult acquire(AcquireContext acquireContext, AcquireChain acquireChain) {
                throw new RuntimeException();
            }

            @Override
            public void release(ReleaseContext releaseContext, ReleaseChain releaseChain) {

            }
        });
        handlerList.add(new TestHandler("T4"));

        Chain chain = new Chain(handlerList);
        LockHandler lockHandler = handlerList.get(0);
        try {
            lockHandler.acquire(acquireContext, chain);
        } catch (Exception ex) {
            // Ignore.
        }

        Assert.assertEquals(2, chain.getAcquireCurrentIndex());
    }

    @Test
    public void releaseException() {
        ReleaseContextBuilder releaseContextBuilder = new ReleaseContextBuilder("R", "V");
        ReleaseContext releaseContext = releaseContextBuilder
                .build();
        List<LockHandler> handlerList = new ArrayList<>();
        handlerList.add(new TestHandler("T1"));
        handlerList.add(new TestHandler("T2"));
        handlerList.add(new LockHandler() {
            @Override
            public AcquireResult acquire(AcquireContext acquireContext, AcquireChain acquireChain) {
                throw new RuntimeException();
            }

            @Override
            public void release(ReleaseContext releaseContext, ReleaseChain releaseChain) {
                throw new RuntimeException();
            }
        });
        handlerList.add(new TestHandler("T4"));

        Chain chain = new Chain(handlerList);
        LockHandler lockHandler = handlerList.get(0);
        try {
            lockHandler.release(releaseContext, chain);
        } catch (Exception ex) {
            // Ignore.
        }

        Assert.assertEquals(2, chain.getReleaseCurrentIndex());
    }

    @Test
    public void release() {
        ReleaseContextBuilder releaseContextBuilder = new ReleaseContextBuilder("R", "V");
        ReleaseContext releaseContext = releaseContextBuilder.build();

        LockHandler lockHandler = handlerList.get(0);
        Chain chain = new Chain(handlerList);

        lockHandler.release(releaseContext, chain);
    }

    static class TestHandler implements LockHandler, ErrorAware {

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

        @Override
        public void onAcquireError(AcquireContext acquireContext, Throwable throwable) {
            System.out.println("onAcquireError on " + name + ". ex=" + throwable);
        }

        @Override
        public void onReleaseError(ReleaseContext releaseContext, Throwable throwable) {
            System.out.println("onReleaseError on " + name + ". ex=" + throwable);
        }
    }

    static class TailHandler implements LockHandler {

        @Override
        public AcquireResult acquire(AcquireContext acquireContext, AcquireChain acquireChain) {
            System.out.println("TailHandler in, got context " + acquireContext);
            try {
                return new AcquireResultBuilder(true).build();
            } finally {
                System.out.println("TailHandler out");
            }
        }

        @Override
        public void release(ReleaseContext releaseContext, ReleaseChain releaseChain) {
            System.out.println("TailHandler release, got context " + releaseContext);
            releaseChain.invoke(releaseContext);
            System.out.println("TailHandler release out");
        }
    }

}