package io.github.weipeng2k.distribute.lock.spi;

/**
 * @author weipeng2k 2021年11月11日 下午17:14:08
 */
public interface ErrorAware {

    void onAcquireError(AcquireContext acquireContext, Throwable throwable);

    void onReleaseError(ReleaseContext releaseContext, Throwable throwable);
}
