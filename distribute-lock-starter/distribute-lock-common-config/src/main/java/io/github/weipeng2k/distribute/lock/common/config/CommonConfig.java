package io.github.weipeng2k.distribute.lock.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author weipeng2k 2021年11月26日 下午20:53:58
 */
@Configuration
public class CommonConfig {

    @Bean("lockHandlerFinder")
    @ConditionalOnMissingBean
    public LockHandlerFinder lockHandlerFinder() {
        return new LockHandlerFinder();
    }
}
