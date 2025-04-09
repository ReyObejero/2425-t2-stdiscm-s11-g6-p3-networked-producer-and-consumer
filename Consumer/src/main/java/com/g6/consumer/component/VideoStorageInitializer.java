package com.g6.consumer.component;

import org.springframework.stereotype.Component;

import com.g6.consumer.service.VideoStorageService;

import jakarta.annotation.PostConstruct;

@Component
public class VideoStorageInitializer {
    private final VideoStorageService videoStorageService;

    public VideoStorageInitializer(VideoStorageService videoStorageService) {
        this.videoStorageService = videoStorageService;
    }

    @PostConstruct
    public void init() {
        videoStorageService.run();
    }
}
