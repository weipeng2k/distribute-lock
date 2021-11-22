package io.github.weipeng2k.distribute.lock.spi;

/**
 * <pre>
 * 实现该接口的LockHandler可以在由于异常导致，锁获取/释放失败时收到通知。
 *
 * 如下TestHandler实现了ErrorAware，将会在由于异常导致的锁获取/释放失败时打印异常。
 *
 * <code>class TestHandler implements LockHandler, ErrorAware {</code>
 * <code></code>
 * <code>        private final String name;</code>
 * <code></code>
 * <code>        TestHandler(String name) {</code>
 * <code>            this.name = name;</code>
 * <code>        }</code>
 * <code></code>
 * <code>       public void onAcquireError(AcquireContext acquireContext, Throwable throwable) {</code>
 * <code>            System.out.println("onAcquireError on " + name + ". ex=" + throwable);</code>
 * <code>        }</code>
 * <code></code>
 * <code>        public void onReleaseError(ReleaseContext releaseContext, Throwable throwable) {</code>
 * <code>            System.out.println("onReleaseError on " + name + ". ex=" + throwable);</code>
 * <code>        }</code>
 * <code>    }</code>
 * </pre></code>
 * <p>
 * 注意：如果调用链路没有执行到实现了该接口的{@link LockHandler}，则不会收到异常通知。
 *
 * @author weipeng2k 2021年11月11日 下午17:14:08
 */
public interface ErrorAware {

    /**
     * 获取锁出现异常失败
     *
     * @param acquireContext 获取上下文
     * @param throwable      异常
     */
    void onAcquireError(AcquireContext acquireContext, Throwable throwable);

    /**
     * 释放锁出现异常失败
     *
     * @param releaseContext 释放锁上下文
     * @param throwable      异常
     */
    void onReleaseError(ReleaseContext releaseContext, Throwable throwable);
}
