package io.github.weipeng2k.distribute.lock.test.support;

/**
 * @author weipeng2k 2021年11月14日 下午16:53:37
 */
public final class CommandLineHelper {

    /**
     * 获取命令行上的重复次数参数
     *
     * @param args         命令行参数
     * @param defaultTimes 默认次数
     * @return 重复次数
     */
    public static int getTimes(String[] args, int defaultTimes) {
        int times = defaultTimes;
        if (args != null && args.length > 0) {
            times = Integer.parseInt(args[0]);
        }

        return times;
    }

    /**
     * 获取命令行上的并发度
     *
     * @param args                   命令行参数
     * @param defaultConcurrentLevel 默认并行度
     * @return 并行度
     */
    public static int getConcurrentLevel(String[] args, int defaultConcurrentLevel) {
        int concurrentLevel = defaultConcurrentLevel;
        if (args != null && args.length > 1) {
            concurrentLevel = Integer.parseInt(args[1]);
        }

        return concurrentLevel;
    }
}
