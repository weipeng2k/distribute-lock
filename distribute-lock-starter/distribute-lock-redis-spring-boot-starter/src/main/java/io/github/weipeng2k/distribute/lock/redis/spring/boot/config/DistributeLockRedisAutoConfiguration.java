package io.github.weipeng2k.distribute.lock.redis.spring.boot.config;

import io.github.weipeng2k.distribute.lock.client.DistributeLockManager;
import io.github.weipeng2k.distribute.lock.client.impl.DistributeLockManagerImpl;
import io.github.weipeng2k.distribute.lock.common.config.CommonConfig;
import io.github.weipeng2k.distribute.lock.common.config.LockHandlerFinder;
import io.github.weipeng2k.distribute.lock.spi.LockHandlerFactory;
import io.github.weipeng2k.distribute.lock.spi.LockRemoteResource;
import io.github.weipeng2k.distribute.lock.spi.impl.LockHandlerFactoryImpl;
import io.github.weipeng2k.distribute.lock.support.redis.RedissonLockRemoteResource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

/**
 * @author weipeng2k 2021年11月26日 下午21:57:15
 */
@Configuration
@ConditionalOnProperty(prefix = Constants.PREFIX, name = "address")
@ConditionalOnClass(RedissonLockRemoteResource.class)
public class DistributeLockRedisAutoConfiguration implements EnvironmentAware {

    private Environment environment;

    @Bean("redisLockRemoteResource")
    public LockRemoteResource lockRemoteResource() {
        Binder binder = Binder.get(environment);
        BindResult<RedisProperties> bindResult = binder.bind(Constants.PREFIX,
                Bindable.of(RedisProperties.class));
        RedisProperties redisProperties = bindResult.get();

        return new RedissonLockRemoteResource(redisProperties.getAddress(), redisProperties.getOwnSecond());
    }

    @Bean("redisLockHandlerFactory")
    public LockHandlerFactory lockHandlerFactory(@Qualifier("lockHandlerFinder") LockHandlerFinder lockHandlerFinder,
                                                 @Qualifier("redisLockRemoteResource") LockRemoteResource lockRemoteResource) {
        return new LockHandlerFactoryImpl(lockHandlerFinder.getLockHandlers(), lockRemoteResource);
    }

    @Bean("redisDistributeLockManager")
    public DistributeLockManager distributeLockManager(
            @Qualifier("redisLockHandlerFactory") LockHandlerFactory lockHandlerFactory) {
        return new DistributeLockManagerImpl(lockHandlerFactory);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
