package io.github.weipeng2k.distribute.lock.plugin.logging;

import io.github.weipeng2k.distribute.lock.spi.AcquireContext;
import io.github.weipeng2k.distribute.lock.spi.AcquireResult;
import io.github.weipeng2k.distribute.lock.spi.ErrorAware;
import io.github.weipeng2k.distribute.lock.spi.LockHandler;
import io.github.weipeng2k.distribute.lock.spi.ReleaseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * 日志输出Handler，打印获取锁和释放锁的日志
 * </pre>
 *
 * @author weipeng2k 2021年11月27日 下午20:44:18
 */
@Order(1)
public class LoggingLockHandler implements LockHandler, ErrorAware {

    private static final Logger logger = LoggerFactory.getLogger("DISTRIBUTE_LOCK_LOGGER");

    @Override
    public AcquireResult acquire(AcquireContext acquireContext, AcquireChain acquireChain) throws InterruptedException {
        AcquireResult acquireResult = acquireChain.invoke(acquireContext);

        logger.info("acquire|{}|{}|{}|{}", acquireContext.getResourceName(), acquireContext.getResourceValue(),
                acquireResult.isSuccess(),
                TimeUnit.MILLISECONDS.convert(System.nanoTime() - acquireContext.getStartNanoTime(),
                        TimeUnit.NANOSECONDS));

        return acquireResult;
    }

    @Override
    public void release(ReleaseContext releaseContext, ReleaseChain releaseChain) {
        releaseChain.invoke(releaseContext);

        logger.info("release|{}|{}", releaseContext.getResourceName(), releaseContext.getResourceValue());
    }

    @Override
    public void onAcquireError(AcquireContext acquireContext, Throwable throwable) {
        logger.error("acquire|{}|{}|{}|{}", acquireContext.getResourceName(), acquireContext.getResourceValue(),
                false,
                TimeUnit.MILLISECONDS.convert(System.nanoTime() - acquireContext.getStartNanoTime(),
                        TimeUnit.NANOSECONDS), throwable);
    }

    @Override
    public void onReleaseError(ReleaseContext releaseContext, Throwable throwable) {
        logger.error("release|{}|{}", releaseContext.getResourceName(), releaseContext.getResourceValue(), throwable);
    }
}
