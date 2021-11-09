package io.github.weipeng2k.distribute.lock.spi.support;

import io.github.weipeng2k.distribute.lock.spi.AcquireResult;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author weipeng2k 2021年11月09日 下午21:42:54
 */
public class AcquireResultBuilderTest {

    @Test
    public void failure() {
        AcquireResultBuilder acquireResultBuilder = new AcquireResultBuilder(false);
        AcquireResult acquireResult = acquireResultBuilder
                .failureType(AcquireResult.FailureType.CUSTOM)
                .failureMessage("failed")
                .exception(new RuntimeException())
                .build();
        System.out.println(acquireResult);
        Assert.assertFalse(acquireResult.isSuccess());
        Assert.assertTrue(acquireResult.getFailureType().isPresent());
        Assert.assertSame(acquireResult.getFailureType().get(), AcquireResult.FailureType.CUSTOM);
        Assert.assertTrue(acquireResult.getFailureMessage().isPresent());
        Assert.assertEquals("failed", acquireResult.getFailureMessage().get());
        Assert.assertTrue(acquireResult.getException().isPresent());
        Assert.assertTrue(acquireResult.getException().get() instanceof RuntimeException);
    }

    @Test
    public void build() {
        AcquireResultBuilder acquireResultBuilder = new AcquireResultBuilder(true);
        AcquireResult acquireResult = acquireResultBuilder.build();
        Assert.assertTrue(acquireResult.isSuccess());
    }
}