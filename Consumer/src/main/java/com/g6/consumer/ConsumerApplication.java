package com.g6.consumer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import com.g6.consumer.model.Video;

@SpringBootApplication
@EnableAsync
public class ConsumerApplication {
    @Value("${server.port}")
    private int serverPort;

    @Bean
    public BlockingQueue<Video> videoQueue() {
        return new ArrayBlockingQueue<>(10);
    }

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }
}

