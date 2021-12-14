package io.github.weipeng2k.distribute.lock.redis.local.hotspot.testsuite;

import io.github.weipeng2k.distribute.lock.client.DistributeLock;
import io.github.weipeng2k.distribute.lock.client.DistributeLockManager;
import io.github.weipeng2k.distribute.lock.plugin.local.hotspot.LocalHotSpotLockRepo;
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

/**
 * @author weipeng2k 2021年12月14日 下午22:04:19
 */
@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private DistributeLockManager distributeLockManager;
    @Autowired
    private Counter counter;
    @Autowired
    private LocalHotSpotLockRepo localHotSpotLockRepo;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        localHotSpotLockRepo.createLock("lock_key");
        DistributeLock distributeLock = distributeLockManager.getLock("lock_key");
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
        Counter counter(@Value("${spring.distribute-lock.counter.host}") String host,
                        @Value("${spring.distribute-lock.counter.port}") int port) {
            return new Counter(host, port);
        }
    }
}
