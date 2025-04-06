package com.g6.consumer.model;

public class Video {
    private String fileName;
    private byte[] fileData;

    public Video(String fileName, byte[] fileData) {
        this.fileName = fileName;
        this.fileData = fileData;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getFileData() {
        return fileData;
    }
}