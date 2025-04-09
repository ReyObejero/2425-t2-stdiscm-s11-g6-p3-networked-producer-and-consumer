package com.g6.consumer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Transient
    private byte[] fileData;

    @Column(name = "file_hash")
    private String fileHash;

    protected Video() {}

    public Video(String fileName, byte[] fileData, String fileHash) {
        this.fileName = fileName;
        this.fileData = fileData;
        this.fileHash = fileHash;
    }

    public int getId() {
        return id;
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