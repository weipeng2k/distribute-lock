package io.github.weipeng2k.distribute.lock.common.config;

import io.github.weipeng2k.distribute.lock.spi.LockHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author weipeng2k 2021年11月26日 下午19:25:08
 */
public class LockHandlerFinder implements ApplicationContextAware, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(LockHandlerFinder.class);

    private ApplicationContext applicationContext;

    private List<LockHandler> lockHandlers;

    /**
     * 获取应用中的LockHandler
     *
     * @return LockHandler列表
     */
    public List<LockHandler> getLockHandlers() {
        return lockHandlers;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, LockHandler> lockHandlerMap = applicationContext.getBeansOfType(LockHandler.class);
        lockHandlers = lockHandlerMap.values().stream()
                .sorted(new AnnotationAwareOrderComparator())
                .collect(Collectors.toList());
        logger.info("[LockHandlerFinder] find {} LockHandler(s): ", lockHandlers.size());
        lockHandlers.forEach(
                handler -> logger.info("[LockHandlerFinder] lock handler: {}.", handler.getClass().getSimpleName()));
    }
}
