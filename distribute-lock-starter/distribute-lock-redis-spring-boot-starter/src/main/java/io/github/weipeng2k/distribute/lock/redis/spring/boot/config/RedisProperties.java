package io.github.weipeng2k.distribute.lock.redis.spring.boot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author weipeng2k 2021年11月26日 下午23:22:11
 */
//@ConfigurationProperties(prefix = Constants.PREFIX)
public class RedisProperties {

    private String address;

    private int ownSecond = 10;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getOwnSecond() {
        return ownSecond;
    }

    public void setOwnSecond(int ownSecond) {
        this.ownSecond = ownSecond;
    }
}
