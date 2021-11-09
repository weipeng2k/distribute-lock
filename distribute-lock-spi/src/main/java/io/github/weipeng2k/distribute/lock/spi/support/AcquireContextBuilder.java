package io.github.weipeng2k.distribute.lock.spi.support;

import io.github.weipeng2k.distribute.lock.spi.AcquireContext;

import java.util.concurrent.TimeUnit;

/**
 * @author weipeng2k 2021年11月09日 下午22:25:56
 */
public final class AcquireContextBuilder {

    private final String resourceName;

    private final String resourceValue;

    private long startNanoTime;

    private long endNanoTime;

    public AcquireContextBuilder(String resourceName, String resourceValue) {
        this.resourceName = resourceName;
        this.resourceValue = resourceValue;
    }

    public AcquireContextBuilder start(long startNanoTime) {
        this.startNanoTime = startNanoTime;
        return this;
    }

    public AcquireContextBuilder timeout(long time, TimeUnit timeUnit) {
        endNanoTime = startNanoTime + timeUnit.toNanos(time);
        return this;
    }

    public AcquireContext build() {
        return new AcquireContextSupport(resourceName, resourceValue, startNanoTime, endNanoTime);
    }

    private static class AcquireContextSupport implements AcquireContext {

        private final String resourceName;

        private final String resourceValue;

        private final long startNanoTime;

        private final long endNanoTime;

        public AcquireContextSupport(String resourceName, String resourceValue, long startNanoTime,
                                     long endNanoTime) {
            this.resourceName = resourceName;
            this.resourceValue = resourceValue;
            this.startNanoTime = startNanoTime;
            this.endNanoTime = endNanoTime;
        }

        @Override
        public String getResourceName() {
            return resourceName;
        }

        @Override
        public String getResourceValue() {
            return resourceValue;
        }

        @Override
        public long getStartNanoTime() {
            return startNanoTime;
        }

        @Override
        public long getEndNanoTime() {
            return endNanoTime;
        }

        @Override
        public String toString() {
            return "AcquireContextSupport{" +
                    "resourceName='" + resourceName + '\'' +
                    ", resourceValue='" + resourceValue + '\'' +
                    ", startNanoTime=" + startNanoTime +
                    ", endNanoTime=" + endNanoTime +
                    '}';
        }
    }
}
