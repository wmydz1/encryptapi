package com.encryptmsg.msgdog.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig {

    private final static Logger logger = LoggerFactory.getLogger(ThreadPoolConfig.class);

    @Value("${asyncThreadPool.corePoolSize}")
    private int corePoolSize;

    @Value("${asyncThreadPool.maxPoolSize}")
    private int maxPoolSize;

    @Value("${asyncThreadPool.queueCapacity}")
    private int queueCapacity;

    @Value("${asyncThreadPool.keepAliveSeconds}")
    private int keepAliveSeconds;

    @Value("${asyncThreadPool.awaitTerminationSeconds}")
    private int awaitTerminationSeconds;

    @Value("${asyncThreadPool.threadNamePrefix}")
    private String threadNamePrefix;

    @Bean(name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        threadPoolTaskExecutor.setQueueCapacity(keepAliveSeconds);
        threadPoolTaskExecutor.setKeepAliveSeconds(queueCapacity);
        threadPoolTaskExecutor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        threadPoolTaskExecutor.setThreadNamePrefix(threadNamePrefix);
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}