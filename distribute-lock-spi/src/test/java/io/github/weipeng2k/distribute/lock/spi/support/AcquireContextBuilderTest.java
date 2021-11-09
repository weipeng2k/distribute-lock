package io.github.weipeng2k.distribute.lock.spi.support;

import io.github.weipeng2k.distribute.lock.spi.AcquireContext;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author weipeng2k 2021年11月09日 下午22:48:59
 */
public class AcquireContextBuilderTest {

    @Test
    public void build() {
        AcquireContextBuilder acquireContextBuilder = new AcquireContextBuilder("resource", "value");
        acquireContextBuilder.start(System.currentTimeMillis());
        acquireContextBuilder.timeout(3, TimeUnit.SECONDS);
        AcquireContext build = acquireContextBuilder.build();
        System.out.println(build);

        assertEquals("resource", build.getResourceName());
        assertEquals("value", build.getResourceValue());
        assertEquals(3000_000_000L, build.getEndNanoTime() - build.getStartNanoTime());
    }
}