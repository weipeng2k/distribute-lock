package io.github.weipeng2k.distribute.lock.spi;

import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * 对锁实现的抽象，任何存储或系统只要可以满足以下获取与释放语义，就可以使用该接口整合如该框架
 * </pre>
 *
 * @author weipeng2k 2021年11月07日 下午20:11:40
 */
public interface LockRemoteResource {

    /**
     * <pre>
     * 在time时间内，尝试对资源resourceName进行获取，如果获取到则将resourceValue进行设置
     * </pre>
     *
     * @param resourceName  资源名称
     * @param resourceValue 资源值
     * @param waitTime      等待时间
     * @param timeUnit      单位
     * @return true，表示获取成功，如果没有获取到则返回false
     */
    AcquireResult tryAcquire(String resourceName, String resourceValue, long waitTime,
                             TimeUnit timeUnit) throws InterruptedException;

    /**
     * <pre>
     * 释放资源名称为resourceName且资源值为resourceValue的资源
     * </pre>
     *
     * @param resourceName  资源名称
     * @param resourceValue 资源值
     */
    void release(String resourceName, String resourceValue);

}
