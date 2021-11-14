package io.github.weipeng2k.distribute.lock.redlock.testsuite;

import io.github.weipeng2k.distribute.lock.client.DistributeLock;
import io.github.weipeng2k.distribute.lock.client.DistributeLockManager;
import io.github.weipeng2k.distribute.lock.client.impl.DistributeLockManagerImpl;
import io.github.weipeng2k.distribute.lock.spi.LockHandlerFactory;
import io.github.weipeng2k.distribute.lock.spi.LockRemoteResource;
import io.github.weipeng2k.distribute.lock.spi.impl.LockHandlerFactoryImpl;
import io.github.weipeng2k.distribute.lock.support.redlock.RedlockLockRemoteResource;
import io.github.weipeng2k.distribute.lock.test.support.CommandLineHelper;
import io.github.weipeng2k.distribute.lock.test.support.Counter;
import io.github.weipeng2k.distribute.lock.test.support.DLTester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * @author weipeng2k 2021年11月14日 下午19:46:15
 */
@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private DistributeLock distributeLock;
    @Autowired
    private Counter counter;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        int times = CommandLineHelper.getTimes(args, 1000);
        DLTester dlTester = new DLTester(distributeLock, 3);
        dlTester.work(times, () -> {
            int i = counter.get();
            i++;
            counter.set(i);
        });

        dlTester.status();
        System.out.println("counter value:" + counter.get());
    }

    @Configuration
    static class Config {
        @Bean
        DistributeLock distributeLock(LockHandlerFactory lockHandlerFactory) {
            DistributeLockManager distributeLockManager = new DistributeLockManagerImpl(lockHandlerFactory);

            return distributeLockManager.getLock("lock_key");
        }

        @Bean
        LockHandlerFactory lockHandlerFactory(LockRemoteResource lockRemoteResource) {
            return new LockHandlerFactoryImpl(Collections.emptyList(), lockRemoteResource);
        }

        @Bean
        LockRemoteResource lockRemoteResource(@Value("${spring.distribute-lock.addresses}") String[] addresses,
                                              @Value("${spring.distribute-lock.own-second}") int ownSecond) {

            return new RedlockLockRemoteResource(addresses, ownSecond);
        }

        @Bean
        Counter counter(@Value("${spring.distribute-lock.counter.host}") String host,
                        @Value("${spring.distribute-lock.counter.port}") int port) {
            return new Counter(host, port);
        }
    }
}

