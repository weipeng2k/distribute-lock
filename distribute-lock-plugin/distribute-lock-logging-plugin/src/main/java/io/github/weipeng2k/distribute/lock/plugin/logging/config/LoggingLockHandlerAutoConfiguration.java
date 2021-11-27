package io.github.weipeng2k.distribute.lock.plugin.logging.config;

import io.github.weipeng2k.distribute.lock.plugin.logging.LoggingLockHandler;
import io.github.weipeng2k.distribute.lock.spi.LockHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author weipeng2k 2021年11月27日 下午21:26:38
 */
@Configuration
@ConditionalOnClass(LoggingLockHandler.class)
public class LoggingLockHandlerAutoConfiguration {

    @Bean("loggingLockHandler")
    public LockHandler lockHandler() {
        return new LoggingLockHandler();
    }
}
