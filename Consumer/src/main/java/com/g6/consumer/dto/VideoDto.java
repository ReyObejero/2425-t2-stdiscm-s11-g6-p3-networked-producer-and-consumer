package com.g6.consumer.dto;

public class VideoDto {
    private String fileName;
    private String fileHash;

    public VideoDto(String fileName, String fileHash) {
        this.fileName = fileName;
        this.fileHash = fileHash;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileHash() {
        return fileHash;
    }
}