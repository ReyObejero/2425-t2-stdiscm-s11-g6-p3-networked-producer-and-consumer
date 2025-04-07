package com.g6.consumer.service;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.UUID;

import com.g6.consumer.model.Video;

public class SocketConnectionService implements Runnable {
    private final Socket socket;
    private final BlockingQueue<Video> videoQueue;

    public SocketConnectionService(Socket socket, BlockingQueue<Video> videoQueue) {
        this.socket = socket;
        this.videoQueue = videoQueue;
    }

    @Override
    public void run() {
        try (DataInputStream dis = new DataInputStream(socket.getInputStream())) {
            String originalFileName = readFileName(dis);
            byte[] fileData = readFileData(dis, dis.readLong());
            String fileHash = readFileHash(dis);

            String fileExtension = getFileExtension(originalFileName);
            String generatedName = generateVideoFileName(fileExtension);

            Video video = new Video(generatedName, fileData, fileHash);

            enqueueVideo(video);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSocket();
        }
    }

    private String readFileName(DataInputStream dis) throws IOException {
        return dis.readUTF();
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

    private String readFileHash(DataInputStream dis) throws IOException {
        return dis.readUTF();
    }

    private void enqueueVideo(Video video) {
        boolean enqueued = videoQueue.offer(video);

        if (enqueued) {
            System.out.println("Enqueued file: " + video.getFileName());
        } else {
            System.out.println("Queue full. Dropping file: " + video.getFileName());
        }
    }

    private void closeSocket() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) {}
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
}
