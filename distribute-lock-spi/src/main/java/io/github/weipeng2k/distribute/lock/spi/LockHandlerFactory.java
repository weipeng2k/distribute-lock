package io.github.weipeng2k.distribute.lock.spi;

import java.util.List;

/**
 * <pre>
 * LockHandler工厂
 *
 * 该工厂可以提供获取LockHandler链的头节点以及责任链相应的Chain，主要功能包括：
 *
 * <li>获取头节点</li> 获取和释放锁需要进行责任链的操作
 * <li>获取Chain</li> 责任链递归操作的入口
 * <li>获取所有LockHandler</li>
 *
 * </pre>
 *
 * @author weipeng2k 2021年11月10日 下午17:50:34
 */
public interface LockHandlerFactory {

    /**
     * 返回LockHandler头节点
     *
     * @return LockHandler头节点
     */
    LockHandler getHead();

    /**
     * 返回获取锁的Chain
     *
     * @return 获取锁的Chain
     */
    LockHandler.AcquireChain getAcquireChain();

    /**
     * 返回释放锁的Chain
     *
     * @return 释放锁的Chain
     */
    LockHandler.ReleaseChain getReleaseChain();

    /**
     * 获取所有的LockHandler
     *
     * @return LockHandler列表
     */
    List<LockHandler> getHandlers();

}
