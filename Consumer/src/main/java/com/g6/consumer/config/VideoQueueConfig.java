package com.g6.consumer.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.g6.consumer.model.Video;

@Configuration
public class VideoQueueConfig {
    @Bean
    public BlockingQueue<Video> videoQueue() {
        return new ArrayBlockingQueue<>(10);
    }
}
