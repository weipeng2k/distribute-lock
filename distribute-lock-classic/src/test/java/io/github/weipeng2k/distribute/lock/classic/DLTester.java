package io.github.weipeng2k.distribute.lock.classic;

import io.github.weipeng2k.distribute.lock.client.DistributeLock;

import java.util.concurrent.TimeUnit;

/**
 * @author weipeng2k 2021年11月13日 下午21:23:41
 */
public class DLTester {

    private DistributeLock distributeLock;

    private int lockSuccess;

    private int lockFailure;

    private int waitSecond;

    public DLTester(DistributeLock distributeLock, int waitSecond) {
        this.distributeLock = distributeLock;
        this.waitSecond = waitSecond;
    }

    public void work(int times, Runnable runnable) {
        for (int i = 0; i < times; i++) {
            if (distributeLock.tryLock(waitSecond, TimeUnit.SECONDS)) {
                try {
                    lockSuccess++;
                    runnable.run();
                } finally {
                    distributeLock.unlock();
                }
            } else {
                lockFailure++;
            }
        }
    }

    public void status() {
        System.out.println("lock success:" + lockSuccess);
        System.out.println("lock failure:" + lockFailure);
    }
}
