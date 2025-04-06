package com.g6.consumer.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.g6.consumer.model.Video;

import jakarta.annotation.PostConstruct;

@Service
public class VideoStorageService implements Runnable {
    private final BlockingQueue<Video> videoQueue;

    public VideoStorageService(BlockingQueue<Video> videoQueue) {
        this.videoQueue = videoQueue;
    }

    @PostConstruct
    public void startVideoStorage() {
        new Thread(this).start();
    }

    @Override
    @Async
    public void run() {
        try {
            while (true) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
