package com.g6.consumer.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.g6.consumer.model.Video;
import com.g6.consumer.repository.VideoRepository;

import jakarta.annotation.PreDestroy;

@Service
public class VideoStorageService {
    private final BlockingQueue<Video> videoQueue;
    private final VideoRepository videoRepository;
    private boolean running = true;

    public VideoStorageService(BlockingQueue<Video> videoQueue, VideoRepository videoRepository) {
        this.videoQueue = videoQueue;
        this.videoRepository = videoRepository;
    }

    @Async
    public void run() {
        try {
            while (running) {
                Video video = videoQueue.take();
                storeVideo(video);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    private void storeVideo(Video video) {
        try {
            File videoStorageDirectory = new File("uploads");
            if (!videoStorageDirectory.exists()) {
                videoStorageDirectory.mkdirs();
            }

            File videoFile = new File(videoStorageDirectory, video.getFileName());
            try (FileOutputStream fos =  new FileOutputStream(videoFile)) {
                fos.write(video.getFileData());
            }
            
            videoRepository.save(video);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void stop() {
        running = false;
    }
}
