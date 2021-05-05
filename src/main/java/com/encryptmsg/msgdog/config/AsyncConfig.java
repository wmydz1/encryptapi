package com.encryptmsg.msgdog.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.lang.reflect.Method;
import java.util.Arrays;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(AsyncConfig.class);

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }

    class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

        @Override
        public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
            logger.info("Exception message - {}", throwable.getMessage());
            logger.info("Method name - {}", method.getName());
            logger.info("Parameter values - {}", Arrays.toString(obj));
            if (throwable instanceof Exception) {
                Exception exception = (Exception) throwable;
                logger.info("exception:{}", exception.getMessage());
            }
            throwable.printStackTrace();
        }
    }
}