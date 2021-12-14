package io.github.weipeng2k.distribute.lock.plugin.local.hotspot.impl;

import org.junit.Test;

import java.util.concurrent.locks.Lock;

import static org.junit.Assert.*;

/**
 * @author weipeng2k 2021年12月14日 下午19:37:48
 */
public class LocalHotSpotLockRepoImplTest {

    @Test
    public void createLock() {
        LocalHotSpotLockRepoImpl localHotSpotLockRepo = new LocalHotSpotLockRepoImpl();
        assertNull(localHotSpotLockRepo.createLock(null));

        Lock x = localHotSpotLockRepo.createLock("x");
        Lock y = localHotSpotLockRepo.createLock("x");

        assertSame(x, y);
        assertNotNull(x);
    }

    @Test
    public void getLock() {
        LocalHotSpotLockRepoImpl localHotSpotLockRepo = new LocalHotSpotLockRepoImpl();
        assertNull(localHotSpotLockRepo.getLock(null));
        assertNull(localHotSpotLockRepo.getLock("none"));

        localHotSpotLockRepo.createLock("x");

        assertNotNull(localHotSpotLockRepo.getLock("x"));
    }

    @Test
    public void removeLock() {
        LocalHotSpotLockRepoImpl localHotSpotLockRepo = new LocalHotSpotLockRepoImpl();

        assertNull(localHotSpotLockRepo.removeLock(null));
        assertNull(localHotSpotLockRepo.removeLock("none"));

        localHotSpotLockRepo.createLock("x");

        assertNotNull(localHotSpotLockRepo.removeLock("x"));
    }

    @Test
    public void clearLocks() {
        LocalHotSpotLockRepoImpl localHotSpotLockRepo = new LocalHotSpotLockRepoImpl();
        localHotSpotLockRepo.createLock("x");

        assertNotNull(localHotSpotLockRepo.getLock("x"));

        localHotSpotLockRepo.clearLocks();

        assertNull(localHotSpotLockRepo.getLock("x"));
    }
}