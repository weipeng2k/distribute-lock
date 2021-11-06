package io.github.weipeng2k.distribute.lock.lettuce.sample;

import io.lettuce.core.RedisClient;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author weipeng2k 2021年11月06日 下午15:06:38
 */
public class ScriptTest {

    @Test
    public void add() {
        RedisClient redisClient = RedisClient.create("redis://1.117.164.80:6379");
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        // 同步执行RedisCommand
        RedisCommands<String, String> syncCommands = connection.sync();
        Long eval = syncCommands.eval("return 1 + 1", ScriptOutputType.INTEGER);
        System.out.println(eval);
    }

    @Test
    public void cad() {
        RedisClient redisClient = RedisClient.create("redis://1.117.164.80:6379");
        // 创建链接，该链接线程安全
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        // 同步执行RedisCommand
        RedisCommands<String, String> syncCommands = connection.sync();

        Long eval = syncCommands.eval(
                "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end",
                ScriptOutputType.INTEGER, new String[]{"key"}, "no");

        System.out.println(eval);

        eval = syncCommands.eval(
                "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end",
                ScriptOutputType.INTEGER, new String[] {"key"}, "Hello, Redis!");

        System.out.println(eval);
    }

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
}
