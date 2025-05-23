package com.g6.consumer.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.g6.consumer.dto.VideoDto;
import com.g6.consumer.repository.VideoRepository;

@RestController
@RequestMapping("/api/videos")
public class VideoController {
    private static final String VIDEO_STORAGE_DIRECTORY = "datastore/videos";

    @Autowired
    private VideoRepository videoRepository;
    @GetMapping
    public List<VideoDto> listVideos() {
        return videoRepository.findAll()
                .stream()
                .map(video -> new VideoDto(video.getFileName(), video.getFileHash()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getVideo(@PathVariable String filename) throws IOException {
        File file = new File(VIDEO_STORAGE_DIRECTORY, filename);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
