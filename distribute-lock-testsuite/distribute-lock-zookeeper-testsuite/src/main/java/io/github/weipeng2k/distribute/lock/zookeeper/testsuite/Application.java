package io.github.weipeng2k.distribute.lock.zookeeper.testsuite;

import io.github.weipeng2k.distribute.lock.client.DistributeLock;
import io.github.weipeng2k.distribute.lock.client.DistributeLockManager;
import io.github.weipeng2k.distribute.lock.client.impl.DistributeLockManagerImpl;
import io.github.weipeng2k.distribute.lock.spi.LockHandlerFactory;
import io.github.weipeng2k.distribute.lock.spi.LockRemoteResource;
import io.github.weipeng2k.distribute.lock.spi.impl.LockHandlerFactoryImpl;
import io.github.weipeng2k.distribute.lock.support.zookeeper.ZooKeeperLockRemoteResource;
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
 * @author weipeng2k 2021年11月14日 下午22:49:15
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
        int concurrentLevel = CommandLineHelper.getConcurrentLevel(args, 1);
        DLTester dlTester = new DLTester(distributeLock, 3);
        dlTester.work(times, concurrentLevel, () -> {
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
        LockRemoteResource lockRemoteResource(@Value("${spring.distribute-lock.connect-string}") String connectString,
                                              @Value("${spring.distribute-lock.base-sleep-time-ms}") int baseSleepTimeMs,
                                              @Value("${spring.distribute-lock.max-retries}") int maxRetries,
                                              @Value("${spring.distribute-lock.session-timeout-ms}") int sessionTimeoutMs,
                                              @Value("${spring.distribute-lock.connection-timeout-ms}") int connectionTimeoutMs) {

            return new ZooKeeperLockRemoteResource(connectString, baseSleepTimeMs, maxRetries, sessionTimeoutMs,
                    connectionTimeoutMs);
        }

        @Bean
        Counter counter(@Value("${spring.distribute-lock.counter.host}") String host,
                        @Value("${spring.distribute-lock.counter.port}") int port) {
            return new Counter(host, port);
        }
    }
}
