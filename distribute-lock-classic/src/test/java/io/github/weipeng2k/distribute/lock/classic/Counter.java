package io.github.weipeng2k.distribute.lock.classic;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.sync.RedisCommands;

/**
 * @author weipeng2k 2021年11月13日 下午22:21:10
 */
public class Counter {

    private final RedisCommands<String, String> syncCommands;

    public Counter(String host, int port) {
        RedisURI redisURI = RedisURI.create(host, port);
        syncCommands = RedisClient.create(redisURI).connect().sync();
    }

    public void init() {
        syncCommands.set("counter", "0");
    }

    public int get() {
        return Integer.parseInt(syncCommands.get("counter"));
    }

    public void set(int value) {
        syncCommands.set("counter", String.valueOf(value));
    }

    public static void main(String[] args) {
        Counter counter = new Counter("1.117.164.80", 6379);
        counter.init();
    }
}
