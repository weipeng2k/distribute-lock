package io.github.weipeng2k.distribute.lock.common.config;

import io.github.weipeng2k.distribute.lock.spi.AcquireContext;
import io.github.weipeng2k.distribute.lock.spi.AcquireResult;
import io.github.weipeng2k.distribute.lock.spi.LockHandler;
import io.github.weipeng2k.distribute.lock.spi.ReleaseContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author weipeng2k 2021年11月26日 下午21:08:53
 */
@SpringBootTest(classes = LockHandlerFinderTest.Config.class)
@TestPropertySource(locations = "classpath:application.properties")
@RunWith(SpringRunner.class)
@EnableAutoConfiguration
public class LockHandlerFinderTest {

    @Autowired
    private LockHandlerFinder lockHandlerFinder;

    @Test
    public void getLockHandlers() {
        List<LockHandler> lockHandlers = lockHandlerFinder.getLockHandlers();
        for (LockHandler lockHandler : lockHandlers) {
            System.out.println(lockHandler);
        }

        assertTrue(lockHandlers.get(0).getClass().toString().contains("3"));
    }

    @Configuration
    static class Config {

        @Bean
        LockHandler l1() {
            return new LockHandlerTest1();
        }

        @Bean
        @Order(2)
        LockHandler l2() {
            return new LockHandlerTest2();
        }

        @Bean
        @Order(1)
        LockHandler l3() {
            return new LockHandlerTest3();
        }
    }

    @Order(3)
    static class LockHandlerTest1 implements LockHandler {

        @Override
        public AcquireResult acquire(AcquireContext acquireContext,
                                     AcquireChain acquireChain) throws InterruptedException {
            return null;
        }

        @Override
        public void release(ReleaseContext releaseContext, ReleaseChain releaseChain) {

        }
    }

    @Order(2)
    static class LockHandlerTest2 implements LockHandler {

        @Override
        public AcquireResult acquire(AcquireContext acquireContext,
                                     AcquireChain acquireChain) throws InterruptedException {
            return null;
        }

        @Override
        public void release(ReleaseContext releaseContext, ReleaseChain releaseChain) {

        }
    }

    @Order(1)
    static class LockHandlerTest3 implements LockHandler {

        @Override
        public AcquireResult acquire(AcquireContext acquireContext,
                                     AcquireChain acquireChain) throws InterruptedException {
            return null;
        }

        @Override
        public void release(ReleaseContext releaseContext, ReleaseChain releaseChain) {

        }
    }
}