package com.g6.consumer.model;

public class Video {
    private String fileName;
    private byte[] fileData;
    private String fileHash;

    public Video(String fileName, byte[] fileData, String fileHash) {
        this.fileName = fileName;
        this.fileData = fileData;
        this.fileHash = fileHash;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public String getFileHash() {
        return fileHash;
    }
}