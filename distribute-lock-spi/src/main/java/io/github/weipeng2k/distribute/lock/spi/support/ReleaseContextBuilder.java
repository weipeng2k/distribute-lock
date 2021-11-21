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

    public ReleaseContextBuilder(String resourceName, String resourceValue) {
        this.resourceName = resourceName;
        this.resourceValue = resourceValue;
    }

    public ReleaseContext build() {
        return new ReleaseContextSupport(resourceName, resourceValue);
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

        public ReleaseContextSupport(String resourceName, String resourceValue) {
            this.resourceName = resourceName;
            this.resourceValue = resourceValue;
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
        public String toString() {
            return "ReleaseContextSupport{" +
                    "resourceName='" + resourceName + '\'' +
                    ", resourceValue='" + resourceValue + '\'' +
                    '}';
        }
    }
}
