package io.github.weipeng2k.distribute.lock.plugin.local.hotspot.config;

import io.github.weipeng2k.distribute.lock.plugin.local.hotspot.LocalHotSpotLockHandler;
import io.github.weipeng2k.distribute.lock.plugin.local.hotspot.LocalHotSpotLockRepo;
import io.github.weipeng2k.distribute.lock.plugin.local.hotspot.impl.LocalHotSpotLockRepoImpl;
import io.github.weipeng2k.distribute.lock.spi.LockHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author weipeng2k 2021年12月14日 下午21:49:29
 */
@Configuration
@ConditionalOnClass(LocalHotSpotLockHandler.class)
public class LocalHotSpotLockHandlerAutoConfiguration {

    @Bean("localHotSpotLockRepo")
    public LocalHotSpotLockRepo localHotSpotLockRepo() {
        return new LocalHotSpotLockRepoImpl();
    }

    @Bean("localHotSpotLockHandler")
    public LockHandler lockHandler(@Qualifier("localHotSpotLockRepo") LocalHotSpotLockRepo localHotSpotLockRepo) {
        return new LocalHotSpotLockHandler(localHotSpotLockRepo);
    }
}
