package io.github.weipeng2k.distribute.lock.spi.support;

import io.github.weipeng2k.distribute.lock.spi.ReleaseContext;

/**
 * <pre>
 * 释放锁上下文Builder
 *
 * </pre>
 *
 * @author weipeng2k 2021年11月10日 下午16:46:34
 */
public final class ReleaseContextBuilder {
    /**
     * 资源名称
     */
    private final String resourceName;
    /**
     * 资源值
     */
    private final String resourceValue;
    /**
     * 开始时间
     */
    private long startNanoTime;

    public ReleaseContextBuilder(String resourceName, String resourceValue) {
        this.resourceName = resourceName;
        this.resourceValue = resourceValue;
    }

    /**
     * 设置获取锁开始的时间
     *
     * @param startNanoTime 开始时间，单位：纳秒
     * @return builder
     */
    public ReleaseContextBuilder start(long startNanoTime) {
        this.startNanoTime = startNanoTime;
        return this;
    }

    /**
     * 构造一个释放锁的上下文
     *
     * @return 释放锁上下文
     */
    public ReleaseContext build() {
        return new ReleaseContextSupport(resourceName, resourceValue, startNanoTime);
    }

    private static class ReleaseContextSupport implements ReleaseContext {

        /**
         * 资源名称
         */
        private final String resourceName;
        /**
         * 资源值
         */
        private final String resourceValue;
        /**
         * 开始时间
         */
        private final long startNanoTime;

        public ReleaseContextSupport(String resourceName, String resourceValue, long startNanoTime) {
            this.resourceName = resourceName;
            this.resourceValue = resourceValue;
            this.startNanoTime = startNanoTime;
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
        public String toString() {
            return "ReleaseContextSupport{" +
                    "resourceName='" + resourceName + '\'' +
                    ", resourceValue='" + resourceValue + '\'' +
                    ", startNanoTime=" + startNanoTime +
                    '}';
        }
    }
}
