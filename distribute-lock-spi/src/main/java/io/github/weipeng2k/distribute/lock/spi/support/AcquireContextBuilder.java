package io.github.weipeng2k.distribute.lock.spi.support;

import io.github.weipeng2k.distribute.lock.spi.AcquireContext;

import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * 获取锁上下文Builder
 *
 * </pre>
 *
 * @author weipeng2k 2021年11月09日 下午22:25:56
 */
public final class AcquireContextBuilder {

    /**
     * 资源名称
     */
    private final String resourceName;
    /**
     * 资源值
     */
    private final String resourceValue;
    /**
     * 开始获取锁的时间
     */
    private long startNanoTime;
    /**
     * 获取到锁的最长时间
     */
    private long endNanoTime;

    public AcquireContextBuilder(String resourceName, String resourceValue) {
        this.resourceName = resourceName;
        this.resourceValue = resourceValue;
    }

    /**
     * 设置获取锁开始的时间
     *
     * @param startNanoTime 开始时间，单位：纳秒
     * @return builder
     */
    public AcquireContextBuilder start(long startNanoTime) {
        this.startNanoTime = startNanoTime;
        return this;
    }

    /**
     * 设置获取锁超时的时间
     *
     * @param time     时间
     * @param timeUnit 单位
     * @return builder
     */
    public AcquireContextBuilder timeout(long time, TimeUnit timeUnit) {
        if (startNanoTime == 0) {
            throw new IllegalStateException("startNanoTime is not set.");
        }
        endNanoTime = startNanoTime + timeUnit.toNanos(time);
        return this;
    }

    /**
     * 构造一个获取锁上下文
     *
     * @return 获取锁上下文
     */
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
