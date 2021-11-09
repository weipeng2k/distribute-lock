package io.github.weipeng2k.distribute.lock.spi;

/**
 * @author weipeng2k 2021年11月09日 下午17:56:27
 */
public interface ReleaseContext {

    String getResourceName();

    String getResourceValue();
}
