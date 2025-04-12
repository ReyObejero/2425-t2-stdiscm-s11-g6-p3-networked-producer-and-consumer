package com.g6.consumer.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import com.g6.consumer.config.VideoConfig;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@RestController
@RequestMapping("/executor")
public class ExecutorController {

    @Autowired
    @Qualifier("videoUploadExecutor")
    private ThreadPoolTaskExecutor videoUploadExecutor;

    @Autowired
    @Qualifier("videoStoreExecutor")
    private ThreadPoolTaskExecutor videoStoreExecutor;

    @Autowired
    private VideoConfig videoConfig;

    @PostMapping("/update")
    public ResponseEntity<Map<String, String>> updateExecutorParams(@RequestBody Map<String, Integer> params) {
        try {
            videoConfig.updateExecutors(params.get("consumerThreads"), params.get("consumerThreads"), params.get("storeQueueCapacity"));
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Parameters updated.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Parameters failed to update."));
        }
    }
}
