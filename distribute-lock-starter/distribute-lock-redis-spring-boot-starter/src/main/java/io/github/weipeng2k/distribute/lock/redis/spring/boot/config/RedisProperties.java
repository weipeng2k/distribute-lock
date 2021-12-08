package io.github.weipeng2k.distribute.lock.redis.spring.boot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author weipeng2k 2021年11月26日 下午23:22:11
 */
@ConfigurationProperties(prefix = Constants.PREFIX)
public class RedisProperties {

    private String address;

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
}
