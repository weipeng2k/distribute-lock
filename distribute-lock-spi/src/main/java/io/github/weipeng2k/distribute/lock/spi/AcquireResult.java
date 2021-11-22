package io.github.weipeng2k.distribute.lock.spi;

import java.util.Optional;

/**
 * <pre>
 * 获取资源的结果
 *
 * </pre>
 *
 * @author weipeng2k 2021年11月09日 下午17:50:13
 */
public interface AcquireResult {

    /**
     * <pre>
     * 是否获取成功
     *
     * </pre>
     *
     * @return true表示获取成功，如果是false，可以查看失败类型以及相关的详细信息
     */
    boolean isSuccess();

    /**
     * <pre>
     * 获取锁失败的类型
     * 如果{@link #isSuccess()}返回false，则可以通过该方法获取到失败的类型
     * </pre>
     *
     * @return 失败类型
     */
    Optional<FailureType> getFailureType();

    /**
     * <pre>
     * 获取锁失败的信息
     * 如果{@link #isSuccess()}返回false，则可以通过该方法获取到失败的类型
     * </pre>
     *
     * @return 失败信息
     */
    Optional<String> getFailureMessage();

    /**
     * <pre>
     * 获取锁失败的异常
     * 如果{@link #isSuccess()}返回false，则可以通过该方法获取到失败的异常
     * </pre>
     *
     * @return 失败异常
     */
    Optional<Throwable> getException();

    /**
     * 获取锁失败类型
     */
    enum FailureType {
        /**
         * 超时
         */
        TIME_OUT,
        /**
         * 异常
         */
        EXCEPTION,
        /**
         * 自定义
         */
        CUSTOM
    }
}
