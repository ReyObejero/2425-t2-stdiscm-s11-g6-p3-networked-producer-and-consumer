package com.g6.consumer.socketserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import com.g6.consumer.service.VideoService;

import jakarta.annotation.PreDestroy;

@Component
public class SocketServer {
    private final int PORT = 5000;
    private final TaskExecutor videoUploadExecutor;
    private final VideoService videoService;
    private ServerSocket serverSocket;

    public SocketServer(TaskExecutor videoUploadExecutor, VideoService videoService) {
        this.videoUploadExecutor = videoUploadExecutor;
        this.videoService = videoService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startSocketListener() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            this.serverSocket = server;
            System.out.println("Socket server started on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                videoUploadExecutor.execute(() -> videoService.handleVideoUpload(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void stopSocketServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
