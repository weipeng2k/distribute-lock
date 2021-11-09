package io.github.weipeng2k.distribute.lock.spi.support;

import io.github.weipeng2k.distribute.lock.spi.AcquireResult;

import java.util.Optional;

/**
 * @author weipeng2k 2021年11月09日 下午20:34:12
 */
public final class AcquireResultBuilder {

    private final boolean success;

    private AcquireResult.FailureType failureType;

    private String failureMessage;

    private Throwable exception;

    public AcquireResultBuilder(boolean success) {
        this.success = success;
    }

    public AcquireResultBuilder failureType(AcquireResult.FailureType failureType) {
        this.failureType = failureType;
        return this;
    }

    public AcquireResultBuilder failureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
        return this;
    }

    public AcquireResultBuilder exception(Throwable exception) {
        this.exception = exception;
        return this;
    }

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
