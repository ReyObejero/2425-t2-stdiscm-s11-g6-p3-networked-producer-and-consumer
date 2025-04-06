package com.g6.consumer.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.g6.consumer.model.Video;

import jakarta.annotation.PreDestroy;

@Service
public class SocketServerService {
    private final int PORT = 5000;
    private final ExecutorService executor;
    private final BlockingQueue<Video> videoQueue;

    public SocketServerService(BlockingQueue<Video> videoQueue) {
        this.videoQueue = videoQueue;
        this.executor = Executors.newFixedThreadPool(4);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startSocketListener() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Socket server started on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                executor.submit(new SocketConnectionService(socket, videoQueue));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void stopSocketServer() {
        try {
            executor.shutdown();
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
