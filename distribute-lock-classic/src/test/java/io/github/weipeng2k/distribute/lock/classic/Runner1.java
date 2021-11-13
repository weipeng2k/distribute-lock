package io.github.weipeng2k.distribute.lock.classic;

import io.github.weipeng2k.distribute.lock.client.DistributeLock;
import io.github.weipeng2k.distribute.lock.client.DistributeLockManager;

/**
 * @author weipeng2k 2021年11月13日 下午21:44:26
 */
public class Runner1 {

    public static void main(String[] args) {
        DistributeLockManager distributeLockManager = new DistributeLockManagerImpl("1.117.164.80", 6379, 10, 10, 5);

        DistributeLock lock = distributeLockManager.getLock("lock_key");

        DLTester dlTester = new DLTester(lock, 3);

        Counter counter = new Counter("1.117.164.80", 6379);
        dlTester.work(1000, () -> {
            int i = counter.get();
            i++;
            counter.set(i);
        });

        dlTester.status();
        System.out.println(counter.get());
    }
}
