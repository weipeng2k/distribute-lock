package io.github.weipeng2k.distribute.lock.spi.support;

import io.github.weipeng2k.distribute.lock.spi.AcquireResult;

import java.util.Optional;

/**
 * <pre>
 * 获取锁结果Builder，使用它可以构造一个获取锁的结果。
 * 不论获取锁失败或成功都用{@link AcquireResult}来表示，纵使获取锁失败，也是正常的。
 *
 * </pre>
 *
 * @author weipeng2k 2021年11月09日 下午20:34:12
 */
public final class AcquireResultBuilder {

    /**
     * 获取锁是否成功
     */
    private final boolean success;
    /**
     * 如果失败，失败类型，该值不为空
     */
    private AcquireResult.FailureType failureType;
    /**
     * 失败消息
     */
    private String failureMessage;
    /**
     * 失败异常
     */
    private Throwable exception;

    public AcquireResultBuilder(boolean success) {
        this.success = success;
    }

    /**
     * 设置失败类型
     *
     * @param failureType 获取锁失败类型
     * @return builder
     */
    public AcquireResultBuilder failureType(AcquireResult.FailureType failureType) {
        this.failureType = failureType;
        return this;
    }

    /**
     * 设置获取失败消息
     *
     * @param failureMessage 获取锁失败消息
     * @return builder
     */
    public AcquireResultBuilder failureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
        return this;
    }

    /**
     * 设置获取锁失败的异常
     *
     * @param exception 获取锁失败异常
     * @return builder
     */
    public AcquireResultBuilder exception(Throwable exception) {
        this.exception = exception;
        return this;
    }

    /**
     * 构造一个获取锁的结果
     *
     * @return 获取锁的结果
     */
    public AcquireResult build() {
        return new AcquireResultSupport(success, failureType, failureMessage, exception);
    }

    private static class AcquireResultSupport implements AcquireResult {

        private final boolean success;

        private final AcquireResult.FailureType failureType;

        private final String failureMessage;

        private final Throwable exception;

        public AcquireResultSupport(boolean success,
                                    FailureType failureType, String failureMessage, Throwable exception) {
            this.success = success;
            this.failureType = failureType;
            this.failureMessage = failureMessage;
            this.exception = exception;
        }

        @Override
        public boolean isSuccess() {
            return success;
        }

        @Override
        public Optional<FailureType> getFailureType() {
            return Optional.ofNullable(failureType);
        }

        @Override
        public Optional<String> getFailureMessage() {
            return Optional.ofNullable(failureMessage);
        }

        @Override
        public Optional<Throwable> getException() {
            return Optional.ofNullable(exception);
        }

        @Override
        public String toString() {
            return "AcquireResultSupport{" +
                    "success=" + success +
                    ", failureType=" + failureType +
                    ", failureMessage='" + failureMessage + '\'' +
                    ", exception=" + exception +
                    '}';
        }
    }
}
