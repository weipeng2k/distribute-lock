package io.github.weipeng2k.distribute.lock.spi;

/**
 * <pre>
 * 锁处理器，以责任链模式构建，可以扩展该接口拦截分布式锁的获取与释放链路。
 *
 * 如下TestHandler实现了LockHandler，分别在锁获取以及释放链路上完成参数打印。
 *
 * 如果当前节点需要传递调用，需要调用{@link AcquireChain#acquire(AcquireContext, AcquireChain)}或{@link ReleaseChain#release(ReleaseContext, ReleaseChain)}。
 *
 * <code>class TestHandler implements LockHandler {</code>
 * <code></code>
 * <code>        private final String name;</code>
 * <code></code>
 * <code>        TestHandler(String name) {</code>
 * <code>            this.name = name;</code>
 * <code>        }</code>
 * <code></code>
 * <code>        public AcquireResult acquire(AcquireContext acquireContext, AcquireChain acquireChain) throws InterruptedException {</code>
 * <code>            System.out.println("Enter " + name + ", before acquire");</code>
 * <code>            try {</code>
 * <code>                return acquireChain.invoke(acquireContext);</code>
 * <code>            } finally {</code>
 * <code>                System.out.println("Leave " + name + ", after acquire");</code>
 * <code>            }</code>
 * <code>        }</code>
 * <code></code>
 * <code>        public void release(ReleaseContext releaseContext, ReleaseChain releaseChain) {</code>
 * <code>            System.out.println("Enter " + name + ", before release");</code>
 * <code>            releaseChain.invoke(releaseContext);</code>
 * <code>            System.out.println("Leave " + name + ", after release");</code>
 * <code>        }</code>
 * <code>}</code>
 *
 * 框架本身也会扩展{@link LockHandler}，它与用户自定义的{@link LockHandler}实现一样，被穿在首尾节点之间，而尾节点将会调用{@link LockRemoteResource}。
 *
 * </pre>
 *
 * @author weipeng2k 2021年11月07日 下午21:03:56
 */
public interface LockHandler {

    /**
     * <pre>
     * 获取锁资源的处理逻辑
     *
     * 实现该方法时，如果是可期望的异常，尽量需要自行捕获
     * 如果抛出异常，将会使异常抵达客户端调用处
     * </pre>
     *
     * @param acquireContext 获取上下文
     * @param acquireChain   chain
     * @return 获取结果
     */
    AcquireResult acquire(AcquireContext acquireContext, AcquireChain acquireChain) throws InterruptedException;

    /**
     * <pre>
     * 获取锁后，在释放锁时，会调用该方法
     * </pre>
     *
     * @param releaseContext 释放上下文
     * @param releaseChain   chain
     */
    void release(ReleaseContext releaseContext, ReleaseChain releaseChain);

    /**
     * 获取锁Chain
     */
    interface AcquireChain {
        /**
         * 调用该方法可以通知获取锁链路的下一个节点
         *
         * @param acquireContext 获取锁上下文
         * @return 获取锁结果
         * @throws InterruptedException 中断异常
         */
        AcquireResult invoke(AcquireContext acquireContext) throws InterruptedException;

        /**
         * 返回当前获取锁链路执行的节点index
         *
         * @return 获取锁链路执行的节点index
         */
        int getAcquireCurrentIndex();
    }

    /**
     * 释放锁Chain
     */
    interface ReleaseChain {
        /**
         * 调用该方法可以通知释放锁链路的下一个节点
         *
         * @param releaseContext 释放锁上下文
         */
        void invoke(ReleaseContext releaseContext);

        /**
         * 返回当前释放锁链路执行的节点index
         *
         * @return 释放锁链路执行的节点index
         */
        int getReleaseCurrentIndex();
    }
}
