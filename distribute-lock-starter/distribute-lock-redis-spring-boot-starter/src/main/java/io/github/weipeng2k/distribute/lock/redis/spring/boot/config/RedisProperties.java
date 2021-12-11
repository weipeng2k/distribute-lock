package io.github.weipeng2k.distribute.lock.redis.spring.boot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author weipeng2k 2021年11月26日 下午23:22:11
 */
@ConfigurationProperties(prefix = Constants.PREFIX)
public class RedisProperties {
    /**
     * redis server地址
     */
    private String address;
    /**
     * 访问超时，毫秒
     */
    private int timeoutMillis = 200;
    /**
     * redis的key过期时间
     */
    private int ownSecond = 10;
    /**
     * spin最小时间，毫秒
     */
    private int minSpinMillis = 10;
    /**
     * 随机spin增加的时间，毫秒
     */
    private int randomMillis = 10;

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

    public int getMinSpinMillis() {
        return minSpinMillis;
    }

    public void setMinSpinMillis(int minSpinMillis) {
        this.minSpinMillis = minSpinMillis;
    }

    public int getRandomMillis() {
        return randomMillis;
    }

    public void setRandomMillis(int randomMillis) {
        this.randomMillis = randomMillis;
    }

    public int getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(int timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }
}
