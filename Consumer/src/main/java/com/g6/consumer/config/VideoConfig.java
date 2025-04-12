package com.g6.consumer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class VideoConfig {

    @Bean
    public ThreadPoolTaskExecutor videoUploadExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.initialize();
        return executor;
    }

    @Bean
    public ThreadPoolTaskExecutor videoStoreExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(10);
        executor.initialize();
        return executor;
    }

    public void updateExecutors(int uploadCorePoolSize, int uploadMaxPoolSize, int storeQueueCapacity) {
        videoUploadExecutor().setCorePoolSize(uploadCorePoolSize);
        videoUploadExecutor().setMaxPoolSize(uploadMaxPoolSize);

        // videoStoreExecutor().setCorePoolSize(storeCorePoolSize);
        // videoStoreExecutor().setMaxPoolSize(storeMaxPoolSize);
        videoStoreExecutor().setQueueCapacity(storeQueueCapacity);
    }
}
