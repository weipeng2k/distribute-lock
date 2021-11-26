package io.github.weipeng2k.distribute.lock.support.redis;

/**
 * <pre>
 * 可以设置每次请求分布式锁的ownTime
 * </pre>
 *
 * @author weipeng2k 2021年11月26日 下午18:05:08
 */
public class OwnSecond {

    private static final ThreadLocal<Integer> LIVE_SECOND = new ThreadLocal<>();

    public static void setLiveSecond(int second) {
        if (second <= 0) {
            throw new IllegalArgumentException("live second must greater than 0.");
        }

        LIVE_SECOND.set(second);
    }

    static Integer getLiveSecond() {
        Integer second = LIVE_SECOND.get();
        LIVE_SECOND.set(null);
        return second;
    }
}
