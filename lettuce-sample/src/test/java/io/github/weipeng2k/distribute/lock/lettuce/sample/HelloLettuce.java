package io.github.weipeng2k.distribute.lock.lettuce.sample;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author weipeng2k 2021年10月30日 下午13:17:11
 */
public class HelloLettuce {

    @Test
    public void set() {
        RedisClient redisClient = RedisClient.create("redis://1.117.164.80:6379");
        // 创建链接，该链接线程安全
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        // 同步执行RedisCommand
        RedisCommands<String, String> syncCommands = connection.sync();

        String key = syncCommands.set("key", "Hello, Redis!");

        Assert.assertEquals("OK", key);

        connection.close();
        redisClient.shutdown();
    }

    @Test
    public void get() {
        RedisURI redisURI = RedisURI.builder()
                .withHost("1.117.164.80")
                .withPort(6379)
                .build();
        RedisClient redisClient = RedisClient.create(redisURI);
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> syncCommands = connection.sync();

        String key = syncCommands.get("key");
        System.out.println(key);

        connection.close();
        redisClient.shutdown();
    }
}
