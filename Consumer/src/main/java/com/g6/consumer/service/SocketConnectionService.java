package com.g6.consumer.service;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

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
            // Read file metadata
            String fileName = readFileName(dis);
            byte[] fileData = readFileData(dis, dis.readLong());

            Video video = new Video(fileName, fileData);
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
}
