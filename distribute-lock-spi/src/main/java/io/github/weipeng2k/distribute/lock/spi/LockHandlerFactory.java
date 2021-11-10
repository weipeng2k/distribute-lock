package io.github.weipeng2k.distribute.lock.spi;

import java.util.List;

/**
 * @author weipeng2k 2021年11月10日 下午17:50:34
 */
public interface LockHandlerFactory {

    LockHandler getHead();

    LockHandler getTail();

    LockHandler.AcquireChain getAcquireChain();

    LockHandler.ReleaseChain getReleaseChain();

    List<LockHandler> getHandlers();

}
