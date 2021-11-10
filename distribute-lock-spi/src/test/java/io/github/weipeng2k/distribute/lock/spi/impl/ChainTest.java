package io.github.weipeng2k.distribute.lock.spi.impl;

import io.github.weipeng2k.distribute.lock.spi.AcquireContext;
import io.github.weipeng2k.distribute.lock.spi.AcquireResult;
import io.github.weipeng2k.distribute.lock.spi.LockHandler;
import io.github.weipeng2k.distribute.lock.spi.ReleaseContext;
import io.github.weipeng2k.distribute.lock.spi.impl.Chain;
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
    public void acquire() {
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
    }

    @Test
    public void release() {
        ReleaseContextBuilder releaseContextBuilder = new ReleaseContextBuilder("R", "V");
        ReleaseContext releaseContext = releaseContextBuilder.build();

        LockHandler lockHandler = handlerList.get(3);
        Chain chain = new Chain(handlerList);

        lockHandler.release(releaseContext, chain);
    }

    static class TestHandler implements LockHandler {

        private final String name;

        TestHandler(String name) {
            this.name = name;
        }

        @Override
        public AcquireResult acquire(AcquireContext acquireContext, AcquireChain acquireChain) {
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

    static class TailHandler implements LockHandler {

        @Override
        public AcquireResult acquire(AcquireContext acquireContext, AcquireChain acquireChain) {
            System.out.println("TailHandler in, get context " + acquireContext);
            try {
                return new AcquireResultBuilder(true).build();
            } finally {
                System.out.println("TailHandler out");
            }
        }

        @Override
        public void release(ReleaseContext releaseContext, ReleaseChain releaseChain) {
            System.out.println("TailHandler release, get context " + releaseContext);
            releaseChain.invoke(releaseContext);
            System.out.println("TailHandler release out");
        }
    }


}