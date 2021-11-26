package io.github.weipeng2k.distribute.lock.support.redis;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author weipeng2k 2021年11月26日 下午18:19:17
 */
public class OwnSecondTest {

    @Test
    public void setTime() {
        Integer liveSecond = OwnSecond.getLiveSecond();

        Assert.assertNull(liveSecond);

        OwnSecond.setLiveSecond(1);

        liveSecond = OwnSecond.getLiveSecond();

        Assert.assertEquals(1, liveSecond.intValue());

        Assert.assertNull(OwnSecond.getLiveSecond());
    }
}