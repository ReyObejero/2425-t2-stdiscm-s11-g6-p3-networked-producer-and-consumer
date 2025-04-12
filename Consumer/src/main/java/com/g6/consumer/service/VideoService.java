package com.g6.consumer.service;

import com.g6.consumer.model.Video;
import com.g6.consumer.repository.VideoRepository;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.RejectedExecutionException;

@Service
public class VideoService {
    private final TaskExecutor videoStoreExecutor;
    private final VideoRepository videoRepository;

    public VideoService(TaskExecutor videoStoreExecutor, VideoRepository videoRepository) {
        this.videoStoreExecutor = videoStoreExecutor;
        this.videoRepository = videoRepository;
    }

    public void handleVideoUpload(Socket socket) {
        try (
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream())
        ) {
            String originalFileName = dis.readUTF();
            long fileSize = dis.readLong();
            byte[] fileData = readFileData(dis, fileSize);
            String fileHash = dis.readUTF();

            String fileExtension = getFileExtension(originalFileName);
            String generatedName = generateVideoFileName(fileExtension);

            Video video = new Video(generatedName, fileData, fileHash);

            tryVideoStore(video, dos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException ignored) {}
        }
    }

    private void tryVideoStore(Video video, DataOutputStream dos) throws IOException {
        try {
            videoStoreExecutor.execute(() -> storeVideo(video));
            dos.writeUTF("Successfully uploaded and scheduled for saving: " + video.getFileName());
        } catch (RejectedExecutionException e) {
            dos.writeUTF("Server is busy. Please try again later.");
        }
    }

    private byte[] readFileData(DataInputStream dis, long fileSize) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        long remaining = fileSize;
        int read;
        while (remaining > 0) {
            int toRead = (int) Math.min(buffer.length, remaining);
            read = dis.read(buffer, 0, toRead);
            if (read == -1) break;
            baos.write(buffer, 0, read);
            remaining -= read;
        }
        return baos.toByteArray();
    }

    private String generateVideoFileName(String extension) {
        long timestamp = System.currentTimeMillis();
        String uuid = UUID.randomUUID().toString();
        return "video_" + timestamp + "_" + uuid + (extension.isEmpty() ? "" : "." + extension);
    }

    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1 || lastDot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDot + 1);
    }

    private void storeVideo(Video video) {
        try {
            File videoStorageDirectory = new File("datastore/videos");
            if (!videoStorageDirectory.exists()) {
                videoStorageDirectory.mkdirs();
            }

            File videoFile = new File(videoStorageDirectory, video.getFileName());
            try (FileOutputStream fos = new FileOutputStream(videoFile)) {
                fos.write(video.getFileData());
            }

            videoRepository.save(video);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
